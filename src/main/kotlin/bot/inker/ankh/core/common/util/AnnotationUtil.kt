package bot.inker.ankh.core.common.util

import bot.inker.aig.AnnotationGenerator
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode

object AnnotationUtil {
  @Suppress("UNCHECKED_CAST")
  fun <T : Annotation> create(annotationNode: AnnotationNode, classLoader: ClassLoader): T {
    val annotationClass = Class.forName(Type.getType(annotationNode.desc).className, true, classLoader) as Class<T>
    val propertyValues = annotationNode.values ?: emptyList()
    val propertyMap = LinkedHashMap<String, Any>()
    for (i in 0 until propertyValues.size / 2) {
      propertyMap[propertyValues[2 * i] as String] = propertyValues[2 * i + 1]
    }
    val generator = AnnotationGenerator.getDelegate(annotationClass)
    return generator.instanceNamed { name, type ->
      val value = propertyMap[name] ?: return@instanceNamed null
      if (type.isEnum) {
        value as Array<String>
        check(value[0] == Type.getDescriptor(type)) { "un-match enum type '${value[0]}' in annotation '${annotationNode.desc}'" }
        return@instanceNamed type.enumConstants.firstOrNull { value[1] == (it as Enum<*>).name }
          ?: throw IllegalStateException("un-match enum value '${value[1]}' in annotation '${annotationNode.desc}'")
      } else {
        value
      }
    }
  }
}