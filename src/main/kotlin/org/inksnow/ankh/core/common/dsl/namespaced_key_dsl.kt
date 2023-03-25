package org.inksnow.ankh.core.common.dsl

import net.kyori.adventure.key.Key
import org.bukkit.plugin.Plugin
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer

fun Plugin.key(key: String): Key {
  return Key.key(this.name, key)
}

fun AnkhPluginContainer.key(key: String): Key {
  return Key.key(this.plugin().name, key)
}