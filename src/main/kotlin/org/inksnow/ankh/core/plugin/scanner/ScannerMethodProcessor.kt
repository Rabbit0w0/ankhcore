package org.inksnow.ankh.core.plugin.scanner

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

interface ScannerMethodProcessor {
  fun process(scanner: PluginClassScanner, classNode: ClassNode, methodNode: MethodNode, annotationNode: AnnotationNode)
}