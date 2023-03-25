package bot.inker.ankh.core.common.dsl

import bot.inker.ankh.core.api.plugin.AnkhPluginContainer
import net.kyori.adventure.key.Key
import org.bukkit.plugin.Plugin

fun Plugin.key(key: String): Key {
  return Key.key(this.name, key)
}

fun AnkhPluginContainer.key(key: String): Key {
  return Key.key(this.plugin().name, key)
}