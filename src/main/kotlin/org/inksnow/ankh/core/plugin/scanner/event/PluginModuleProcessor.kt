package org.inksnow.ankh.core.plugin.scanner.event

import com.google.inject.Module
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl
import org.inksnow.ankh.core.plugin.scanner.PluginClassScanner
import org.inksnow.ankh.core.plugin.scanner.ScannerClassProcessor
import org.inksnow.ankh.loader.AnkhClassLoader
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