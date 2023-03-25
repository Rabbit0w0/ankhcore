package bot.inker.ankh.core.item;

import bot.inker.ankh.core.api.item.AnkhItem;
import bot.inker.ankh.core.api.item.AnkhItemRegistry;
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent;
import bot.inker.ankh.core.common.AbstractRegistry;
import bot.inker.ankh.core.libs.nbtapi.NBTItem;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class ItemRegisterService extends AbstractRegistry<AnkhItem> implements AnkhItemRegistry {
  @SubscriptEvent(priority = EventPriority.MONITOR)
  private void onInteractEvent(PlayerInteractEvent event) {
    if (event.getAction() == Action.PHYSICAL) {
      return;
    }
    val item = event.getItem();
    val ankhItem = warpItem(item);
    if(ankhItem == null){
      return;
    }
    if(isUseItem(event)){
      ankhItem.onUseItem(event);
    }
  }

  @SubscriptEvent(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  private void onBlockPlace(BlockPlaceEvent event) {
    val item = event.getItemInHand();
    val ankhItem = warpItem(item);
    if(ankhItem != null){
      ankhItem.onBlockPlace(event);
    }
  }

  private boolean isUseItem(PlayerInteractEvent event){
    // if deny use hand
    if(event.useItemInHand() == Event.Result.DENY){
      return false;
    }
    // if default use block
    if (event.useInteractedBlock() != Event.Result.ALLOW) {
      return false;
    }
    // if clicked block is null
    if (event.getClickedBlock() == null) {
      return true;
    }
    // if sneaking
    if(event.getPlayer().isSneaking()) {
      return true;
    }
    // if clicked block is interactable
    return !event.getClickedBlock().getType().isInteractable();
  }

  private AnkhItem warpItem(ItemStack item) {
    if(item == null){
      return null;
    }
    if(item.getType() == Material.AIR){
      return null;
    }
    val nbtItem = new NBTItem(item);
    val itemId = nbtItem.getString("ankh-core:item-id");

    if (itemId == null || itemId.isEmpty()) {
      return null;
    }

    val ankhItem = get(Key.key(itemId));

    if (ankhItem == null) {
      logger.warn("No ankh-item '{}' found, maybe some extensions not loaded", itemId);
    }
    return ankhItem;
  }
}
