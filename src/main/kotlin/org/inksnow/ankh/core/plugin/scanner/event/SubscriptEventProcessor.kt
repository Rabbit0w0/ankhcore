package org.inksnow.ankh.core.plugin.scanner.event

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent
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
import javax.inject.Inject

class SubscriptEventProcessor @Inject private constructor(
  private val logger: Logger,
  private val ankhClassLoader: AnkhClassLoader,
  private val ankhPluginContainer: AnkhPluginContainerImpl,
) : ScannerMethodProcessor {
  @Suppress("UNCHECKED_CAST")
  override fun process(
    scanner: PluginClassScanner,
    classNode: ClassNode,
    methodNode: MethodNode,
    annotationNode: AnnotationNode,
  ) {
    logger.debug("process SubscriptEvent in {}{}{}", classNode.name, methodNode.name, methodNode.desc)

    ankhPluginContainer.onEnable(EventPriority.NORMAL) {
      val methodType = Type.getMethodType(methodNode.desc)
      val isStaticMethod = (methodNode.access and Opcodes.ACC_STATIC != 0)
      val listenerClass = Class.forName(classNode.name.replace('/', '.'), true, ankhClassLoader)
      val listenerInstance = if (isStaticMethod) {
        null
      } else {
        ankhPluginContainer.injector.getInstance(listenerClass)
      }
      val annotation =
        AnnotationUtil.create<SubscriptEvent>(
          annotationNode,
          ankhClassLoader
        )
      val eventClass = Class.forName(methodType.argumentTypes[0].className, true, ankhClassLoader) as Class<out Event>
      val executor =
        if ((classNode.access and Opcodes.ACC_PUBLIC == 0) || (methodNode.access and Opcodes.ACC_PUBLIC) == 0) {
          AsmEventExecutorFactory.generateDynamicExecutor(listenerClass, listenerInstance, methodNode.name, methodType)
        } else {
          AsmEventExecutorFactory.generateDirectExecutor(listenerClass, listenerInstance, methodNode.name, methodType)
        }
      Bukkit.getPluginManager().registerEvent(
        eventClass,
        ankhPluginContainer.bukkitPlugin,
        annotation.priority,
        executor,
        ankhPluginContainer.bukkitPlugin,
        annotation.ignoreCancelled
      )
    }
  }
}