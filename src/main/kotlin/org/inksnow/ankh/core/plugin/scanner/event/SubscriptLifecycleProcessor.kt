package org.inksnow.ankh.core.plugin.scanner.event

import bot.inker.acj.JvmHacker
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle
import org.inksnow.ankh.core.common.util.AnnotationUtil
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl
import org.inksnow.ankh.core.plugin.scanner.PluginClassScanner
import org.inksnow.ankh.core.plugin.scanner.ScannerMethodProcessor
import org.inksnow.ankh.loader.AnkhClassLoader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.slf4j.Logger
import java.lang.invoke.MethodType
import javax.inject.Inject

class SubscriptLifecycleProcessor @Inject private constructor(
  private val logger: Logger,
  private val ankhClassLoader: AnkhClassLoader,
  private val ankhPluginContainer: AnkhPluginContainerImpl,
) : ScannerMethodProcessor {
  override fun process(
    scanner: PluginClassScanner,
    classNode: ClassNode,
    methodNode: MethodNode,
    annotationNode: AnnotationNode,
  ) {
    logger.debug("process SubscriptLifecycle in {}{}{}", classNode.name, methodNode.name, methodNode.desc)
    val isStaticMethod = (methodNode.access and Opcodes.ACC_STATIC != 0)
    val methodType = Type.getMethodType(methodNode.desc)
    check(methodType.argumentTypes.isEmpty()) { "subscript lifecycle require no-args method" }

    val callMethod = Runnable {
      val ownerClass = Class.forName(classNode.name.replace('/', '.'), true, ankhClassLoader)
      if (isStaticMethod) {
        JvmHacker.lookup().findStatic(
          ownerClass,
          methodNode.name,
          MethodType.fromMethodDescriptorString(methodNode.desc, ownerClass.classLoader)
        ).invoke()
      } else {
        JvmHacker.lookup().findVirtual(
          ownerClass,
          methodNode.name,
          MethodType.fromMethodDescriptorString(methodNode.desc, ownerClass.classLoader)
        ).invoke(ankhPluginContainer.injector.getInstance(ownerClass))
      }
    }

    val annotation =
      AnnotationUtil.create<SubscriptLifecycle>(
        annotationNode,
        ankhClassLoader
      )
    when (annotation.value) {
      PluginLifeCycle.LOAD -> ankhPluginContainer.onLoad(
        annotation.priority,
        callMethod
      )

      PluginLifeCycle.ENABLE -> ankhPluginContainer.onEnable(
        annotation.priority,
        callMethod
      )

      PluginLifeCycle.DISABLE -> ankhPluginContainer.onDisable(
        annotation.priority,
        callMethod
      )
    }
  }
}