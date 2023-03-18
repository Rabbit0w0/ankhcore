package bot.inker.ankh.core.plugin.scanner.event

import bot.inker.ankh.core.plugin.AnkhPluginContainerImpl
import bot.inker.ankh.core.plugin.scanner.PluginClassScanner
import bot.inker.ankh.core.plugin.scanner.ScannerClassProcessor
import bot.inker.ankh.loader.AnkhClassLoader
import com.google.inject.Module
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.slf4j.Logger
import javax.inject.Inject

class PluginModuleProcessor @Inject private constructor(
  private val logger: Logger,
  private val ankhClassLoader: AnkhClassLoader,
  private val ankhPluginContainer: AnkhPluginContainerImpl,
) : ScannerClassProcessor {
  override fun process(scanner: PluginClassScanner, classNode: ClassNode, annotationNode: AnnotationNode) {
    logger.debug("process PluginModule in {}", classNode.name)
    val moduleClass = Class.forName(classNode.name.replace('/', '.'), false, ankhClassLoader)
    ankhPluginContainer.pluginModules.add(moduleClass as Class<out Module>)
  }
}