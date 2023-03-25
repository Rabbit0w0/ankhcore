package org.inksnow.ankh.core.plugin.scanner.event

import org.bukkit.event.EventPriority
import org.inksnow.ankh.core.database.DatabaseService
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl
import org.inksnow.ankh.core.plugin.scanner.PluginClassScanner
import org.inksnow.ankh.core.plugin.scanner.ScannerClassProcessor
import org.inksnow.ankh.loader.AnkhClassLoader
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.slf4j.Logger
import javax.inject.Inject

class EntityProcessor @Inject private constructor(
  private val logger: Logger,
  private val ankhClassLoader: AnkhClassLoader,
  private val ankhPluginContainer: AnkhPluginContainerImpl,
) : ScannerClassProcessor {
  override fun process(scanner: PluginClassScanner, classNode: ClassNode, annotationNode: AnnotationNode) {
    logger.debug("process Entity in {}", classNode.name)

    ankhPluginContainer.onLoad(EventPriority.NORMAL) {
      ankhPluginContainer.injector
        .getInstance(DatabaseService::class.java)
        .registerEntity(
          Class.forName(classNode.name.replace('/', '.'), true, ankhClassLoader)
        )
    }
  }
}