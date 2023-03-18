package bot.inker.ankh.core.plugin.scanner.event

import bot.inker.ankh.core.database.DatabaseService
import bot.inker.ankh.core.plugin.AnkhPluginContainerImpl
import bot.inker.ankh.core.plugin.scanner.PluginClassScanner
import bot.inker.ankh.core.plugin.scanner.ScannerClassProcessor
import bot.inker.ankh.loader.AnkhClassLoader
import org.bukkit.event.EventPriority
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