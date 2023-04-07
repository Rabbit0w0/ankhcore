package org.inksnow.ankh.testplugin

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle
import org.inksnow.ankh.core.inventory.menu.AbstractChestMenu
import org.inksnow.ankh.testplugin.item.TestItem
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestListener @Inject private constructor(
  private val plugin: AnkhBukkitPlugin,
  private val testItem: TestItem,
) {
  private val logger = LoggerFactory.getLogger(this.javaClass)

  @SubscriptEvent
  private fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
    if (!event.message.startsWith("@")) {
      return
    }
    Bukkit.getScheduler().runTask(plugin, Runnable {
      runSimpleCommand(event.player, event.message.substring(1))
    })
  }

  private fun runSimpleCommand(player: Player, command: String) {
    when (command) {
      "a" -> {
        player.inventory.addItem(
          testItem.createItem()
        ).values.forEach {
          player.world.dropItemNaturally(player.location, it)
        }
      }

      "b" -> {
        AbstractChestMenu().openForPlayer(player)
      }
    }
  }

  @SubscriptLifecycle(PluginLifeCycle.LOAD)
  private fun onLoad() {

  }
}