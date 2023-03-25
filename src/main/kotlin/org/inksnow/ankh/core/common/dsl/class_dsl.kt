@file:Suppress("NOTHING_TO_INLINE")

package org.inksnow.ankh.core.common.dsl

import org.inksnow.ankh.core.ScreenPrinter
import org.inksnow.ankh.core.api.plugin.AnkhPluginYml
import org.inksnow.ankh.loader.AnkhClassLoader
import kotlin.reflect.KClass

@JvmName("\$kotlin\$inline\$pluginyml\$1")
inline fun <T> Class<T>.pluginyml(): AnkhPluginYml {
  return pluginyml(this)
}

@JvmName("\$kotlin\$inline\$pluginyml\$2")
inline fun <T : Any> KClass<T>.pluginyml(): AnkhPluginYml {
  return pluginyml(this.java)
}

@JvmName("\$kotlin\$inline\$pluginyml\$3")
inline fun pluginyml(clazz: Class<*>): AnkhPluginYml {
  val classLoader = clazz.classLoader
  val ankhClassLoader = if (classLoader is AnkhClassLoader) {
    classLoader
  } else {
    ScreenPrinter::class.java.classLoader as AnkhClassLoader
  }
  return ankhClassLoader.pluginYml
}