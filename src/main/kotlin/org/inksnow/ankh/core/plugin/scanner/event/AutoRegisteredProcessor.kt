package org.inksnow.ankh.core.plugin.scanner.event

import org.bukkit.event.EventPriority
import org.inksnow.ankh.core.api.block.AnkhBlock
import org.inksnow.ankh.core.api.item.AnkhItem
import org.inksnow.ankh.core.block.BlockRegisterService
import org.inksnow.ankh.core.item.ItemRegisterService
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl
import org.inksnow.ankh.core.plugin.scanner.PluginClassScanner
import org.inksnow.ankh.core.plugin.scanner.ScannerClassProcessor
import org.inksnow.ankh.loader.AnkhClassLoader
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.slf4j.Logger
import javax.inject.Inject

class AutoRegisteredProcessor @Inject private constructor(
  private val logger: Logger,
  private val ankhClassLoader: AnkhClassLoader,
  private val ankhPluginContainer: AnkhPluginContainerImpl,
) : ScannerClassProcessor {
  override fun process(scanner: PluginClassScanner, classNode: ClassNode, annotationNode: AnnotationNode) {
    logger.debug("process AutoRegistered in {}", classNode.name)

    ankhPluginContainer.onLoad(EventPriority.NORMAL) {
      val injector = ankhPluginContainer.injector
      val clazz = Class.forName(classNode.name.replace('/', '.'), true, ankhClassLoader)
      if (AnkhItem::class.java.isAssignableFrom(clazz)) {
        injector.getInstance(ItemRegisterService::class.java)
          .register(injector.getInstance(clazz) as AnkhItem)
      } else if (AnkhBlock.Factory::class.java.isAssignableFrom(clazz)) {
        injector.getInstance(BlockRegisterService::class.java)
          .register(injector.getInstance(clazz) as AnkhBlock.Factory)
      }
    }
  }
}