@file:Suppress("NOTHING_TO_INLINE")

package org.inksnow.ankh.core.common.dsl

import org.inksnow.ankh.loader.AnkhClassLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@JvmName("\$kotlin\$inline\$logger\$0")
inline fun <reified T : Any> T.logger(): DslLogger {
  return logger(T::class.java)
}

@JvmName("\$kotlin\$inline\$logger\$2")
inline fun <T> Class<T>.logger(): DslLogger {
  return logger(this)
}

@JvmName("\$kotlin\$inline\$logger\$3")
inline fun <T : Any> KClass<T>.logger(): DslLogger {
  return logger(this.java)
}

@JvmName("\$kotlin\$inline\$logger\$4")
inline fun logger(clazz: Class<*>): DslLogger {
  val classLoader = clazz.classLoader
  if (classLoader is AnkhClassLoader) {
    return logger(classLoader.pluginName)
  }
  val elements = clazz.name.split('.').let { elements ->
    val lastUpper = elements.indexOfLast {
      if (it.isEmpty()) {
        return@indexOfLast false
      }
      it.first().isLowerCase()
    }
    elements.subList(
      0,
      (lastUpper + 2).coerceIn(0..elements.size)
    )
  }
  return logger(
    elements.joinToString(".", transform = {
      var indexOf = it.indexOf('$')
      if (indexOf == -1) {
        indexOf = it.length
      }
      it.substring(0, indexOf)
    })
  )
}

@JvmName("\$kotlin\$inline\$logger\$5")
inline fun logger(name: String): DslLogger {
  return DslLogger(LoggerFactory.getLogger(name))
}

@JvmInline
value class DslLogger(
  val logger: Logger,
) : Logger by logger {
  inline operator fun <T> getValue(inst: T, property: KProperty<*>): Logger {
    return logger
  }
}