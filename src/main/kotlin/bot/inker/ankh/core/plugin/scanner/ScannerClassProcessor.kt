package bot.inker.ankh.core.plugin.scanner

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

interface ScannerClassProcessor {
  fun process(scanner: PluginClassScanner, classNode: ClassNode, annotationNode: AnnotationNode)
}