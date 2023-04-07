package org.inksnow.ankh.core.plugin.scanner

import com.google.inject.Inject
import com.google.inject.Injector
import jakarta.persistence.Entity
import org.inksnow.ankh.core.api.plugin.annotations.AutoRegistered
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl
import org.inksnow.ankh.core.plugin.scanner.event.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.slf4j.Logger
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL

class PluginClassScanner @Inject private constructor(
  private val injector: Injector,
  private val logger: Logger,
  private val ankhPluginContainer: AnkhPluginContainerImpl,
) {
  private var entryCount = 0
  private var jarCount = 0

  fun scan() {
    val startTime = System.nanoTime()
    ankhPluginContainer.classLoader.urLs.forEach(this::scan)
    val passTime = (System.nanoTime() - startTime) / 1000_000
    logger.info("Scanned {} classes in {} jars in {} ms", entryCount, jarCount, passTime)
  }

  private fun scan(url: URL) {
    val urlConnection = url.openConnection()
    if (urlConnection !is JarURLConnection) {
      return
    }

    val jarFile = urlConnection.jarFile
    jarCount++

    for (jarEntry in jarFile.entries()) {
      if (jarEntry.isDirectory) {
        continue
      }
      if (!jarEntry.name.endsWith(".class")) {
        continue
      }
      entryCount++
      logger.trace("scan class {}", jarEntry.name)

      val classNode = jarFile.getInputStream(jarEntry).use(this::readClassNode)
      if (classNode.visibleAnnotations != null) {
        for (annotationNode in classNode.visibleAnnotations) {
          val annotationType = Type.getType(annotationNode.desc)
          val classProcessor = injector.getInstance(
            classProcessors[annotationType.className] ?: continue
          )
          logger.trace("process {} for {}", annotationType, classNode.name)
          classProcessor.process(this, classNode, annotationNode)
        }
      }

      for (methodNode in classNode.methods) {
        if (methodNode.visibleAnnotations != null) {
          for (annotationNode in methodNode.visibleAnnotations) {
            val annotationType = Type.getType(annotationNode.desc)
            val methodProcessor = injector.getInstance(
              methodProcessors[annotationType.className] ?: continue
            )
            logger.trace(
              "processed {} for {}{}{}",
              annotationType,
              classNode.name,
              methodNode.name,
              methodNode.desc
            )
            methodProcessor.process(this, classNode, methodNode, annotationNode)
          }
        }
      }
    }
  }

  private fun readClassNode(input: InputStream): ClassNode {
    val classReader = ClassReader(input)
    val classNode = ClassNode()
    classReader.accept(classNode, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
    return classNode
  }

  companion object {
    private val classProcessors = mapOf<String, Class<out ScannerClassProcessor>>(
      PluginModule::class.java.name to PluginModuleProcessor::class.java,
      Entity::class.java.name to EntityProcessor::class.java,
      AutoRegistered::class.java.name to AutoRegisteredProcessor::class.java
    )
    private val methodProcessors = mapOf<String, Class<out ScannerMethodProcessor>>(
      SubscriptEvent::class.java.name to SubscriptEventProcessor::class.java,
      SubscriptLifecycle::class.java.name to SubscriptLifecycleProcessor::class.java,
    )
  }
}