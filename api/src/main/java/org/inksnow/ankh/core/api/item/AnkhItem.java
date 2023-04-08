package org.inksnow.ankh.core.api.item;

import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhCore;

public interface AnkhItem extends Keyed {
  NamespacedKey ITEM_ID_KEY = new NamespacedKey(AnkhCore.PLUGIN_ID, "item-id");

  default void updateItem(ItemStack item) {
    //
  }

  default void onBlockPlace(BlockPlaceEvent event) {
    //
  }

  default boolean isUseItem(PlayerInteractEvent event) {
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

  default void acceptInteractEvent(PlayerInteractEvent event) {
    if (isUseItem(event)) {
      onUseItem(event);
    }
  }

  default void onUseItem(PlayerInteractEvent event) {
    //
  }
}
