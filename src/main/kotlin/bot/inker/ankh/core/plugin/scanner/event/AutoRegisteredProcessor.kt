package bot.inker.ankh.core.plugin.scanner.event

import bot.inker.ankh.core.api.block.AnkhBlock
import bot.inker.ankh.core.api.item.AnkhItem
import bot.inker.ankh.core.block.BlockRegisterService
import bot.inker.ankh.core.item.ItemRegisterService
import bot.inker.ankh.core.plugin.AnkhPluginContainerImpl
import bot.inker.ankh.core.plugin.scanner.PluginClassScanner
import bot.inker.ankh.core.plugin.scanner.ScannerClassProcessor
import bot.inker.ankh.loader.AnkhClassLoader
import org.bukkit.event.EventPriority
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