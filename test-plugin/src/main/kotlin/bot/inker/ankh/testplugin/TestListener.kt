package bot.inker.ankh.testplugin

import bot.inker.ankh.core.api.plugin.PluginLifeCycle
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent
import bot.inker.ankh.core.api.plugin.annotations.SubscriptLifecycle
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.testplugin.item.TestItem
import org.bukkit.event.player.AsyncPlayerChatEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestListener @Inject private constructor(
  private val testItem: TestItem,
) {
  private val logger by logger()

  @SubscriptEvent
  private fun onBlockPlace(event: AsyncPlayerChatEvent) {
    if (!event.message.startsWith("@")) {
      return
    }
    val command = event.message.substring(1)
    when (command) {
      "a" -> {
        event.player.inventory.addItem(
          testItem.createItem()
        ).values.forEach {
          event.player.world.dropItemNaturally(event.player.location, it)
        }
      }
    }
  }

  @SubscriptLifecycle(PluginLifeCycle.LOAD)
  private fun onLoad() {

  }
}