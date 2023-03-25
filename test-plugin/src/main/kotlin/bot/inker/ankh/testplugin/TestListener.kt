package bot.inker.ankh.testplugin

import bot.inker.ankh.core.api.plugin.AnkhBukkitPlugin
import bot.inker.ankh.core.api.plugin.PluginLifeCycle
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent
import bot.inker.ankh.core.api.plugin.annotations.SubscriptLifecycle
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.inventory.menu.AbstractChestMenu
import bot.inker.ankh.testplugin.item.TestItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestListener @Inject private constructor(
  private val plugin:AnkhBukkitPlugin,
  private val testItem: TestItem,
) {
  private val logger by logger()

  @SubscriptEvent
  private fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
    if (!event.message.startsWith("@")) {
      return
    }
    Bukkit.getScheduler().runTask(plugin, Runnable {
      runSimpleCommand(event.player, event.message.substring(1))
    })
  }

  private fun runSimpleCommand(player:Player, command:String){
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