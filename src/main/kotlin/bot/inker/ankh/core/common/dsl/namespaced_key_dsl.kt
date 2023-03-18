package bot.inker.ankh.core.common.dsl

import bot.inker.ankh.core.api.plugin.AnkhPluginContainer
import bot.inker.ankh.core.plugin.AnkhPluginContainerImpl
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

fun Plugin.key(key: String): NamespacedKey {
  return NamespacedKey(this, key)
}

fun AnkhPluginContainer.key(key: String): NamespacedKey {
  return NamespacedKey((this as AnkhPluginContainerImpl).pluginYml.name, key)
}