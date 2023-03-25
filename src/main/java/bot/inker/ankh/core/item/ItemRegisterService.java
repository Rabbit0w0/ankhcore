package bot.inker.ankh.core.item;

import bot.inker.ankh.core.api.item.AnkhItem;
import bot.inker.ankh.core.api.item.AnkhItemRegistry;
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent;
import bot.inker.ankh.core.common.AbstractRegistry;
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
import org.bukkit.persistence.PersistentDataType;

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
    if (ankhItem == null) {
      return;
    }
    if (isUseItem(event)) {
      ankhItem.onUseItem(event);
    }
  }

  @SubscriptEvent(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  private void onBlockPlace(BlockPlaceEvent event) {
    val item = event.getItemInHand();
    val ankhItem = warpItem(item);
    if (ankhItem != null) {
      ankhItem.onBlockPlace(event);
    }
  }

  private boolean isUseItem(PlayerInteractEvent event) {
    // if deny use hand
    if (event.useItemInHand() == Event.Result.DENY) {
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
    if (event.getPlayer().isSneaking()) {
      return true;
    }
    // if clicked block is interactable
    return !event.getClickedBlock().getType().isInteractable();
  }

  private AnkhItem warpItem(ItemStack item) {
    if (item == null) {
      return null;
    }
    if (item.getType() == Material.AIR) {
      return null;
    }
    val itemMeta = item.getItemMeta();
    if(itemMeta == null){
      return null;
    }
    val dataContainer = itemMeta.getPersistentDataContainer();
    val itemIdKeyString = dataContainer.get(AnkhItem.ITEM_ID_KEY, PersistentDataType.STRING);
    if(itemIdKeyString == null || itemIdKeyString.isEmpty()){
      return null;
    }

    val ankhItem = get(Key.key(itemIdKeyString));

    if (ankhItem == null) {
      logger.warn("No ankh-item '{}' found, maybe some extensions not loaded", itemIdKeyString);
    }
    return ankhItem;
  }
}
