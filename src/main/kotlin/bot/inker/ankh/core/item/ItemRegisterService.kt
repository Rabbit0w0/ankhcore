package bot.inker.ankh.core.item

import bot.inker.ankh.core.api.item.AnkhItem
import bot.inker.ankh.core.api.item.AnkhItemRegistry
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent
import bot.inker.ankh.core.common.AbstractRegistry
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.libs.nbtapi.NBTItem
import org.bukkit.NamespacedKey
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRegisterService @Inject private constructor(

) : AbstractRegistry<AnkhItem>(), AnkhItemRegistry {
  private val logger by logger()

  @SubscriptEvent(priority = EventPriority.MONITOR)
  private fun onInteractEvent(event: PlayerInteractEvent) {
    if (event.action == Action.PHYSICAL) {
      return
    }
    val item = event.item ?: return
    val ankhItem = warpItem(item) ?: return
    event.player.sendMessage("useItemInHand=" + event.useItemInHand().name + ", useInteractedBlock=" + event.useInteractedBlock().name)
    if ((event.useItemInHand() == Event.Result.ALLOW || event.useItemInHand() == Event.Result.DEFAULT)
      && (event.useInteractedBlock() != Event.Result.ALLOW || event.clickedBlock?.type?.isInteractable != true || event.player.isSneaking)
    ) {
      ankhItem.onUseItem(event)
    }
  }

  @SubscriptEvent(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  private fun onBlockPlace(event: BlockPlaceEvent) {
    val item = event.itemInHand
    val ankhItem = warpItem(item) ?: return
    ankhItem.onBlockPlace(event)
  }

  private fun warpItem(item: ItemStack?): AnkhItem? {
    item ?: return null
    if (item.type.isAir) {
      return null
    }
    val nbtItem = NBTItem(item)
    val itemId = nbtItem.getString("ankh-core:item-id")
    if (itemId.isNullOrEmpty()) {
      return null
    }
    val ankhItem = get(NamespacedKey.fromString(itemId))
    if (ankhItem == null) {
      logger.warn("No ankh-item '{}' found, maybe some extensions not loaded", itemId)
    }
    return ankhItem
  }
}