package org.inksnow.ankh.core.api.item;

import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhCore;

@Deprecated // old item api
public interface AnkhItem extends Keyed {
  NamespacedKey ITEM_ID_KEY = new NamespacedKey(AnkhCore.PLUGIN_ID, "item-id");

  default void updateItem(ItemStack item) {
    //
  }

  default void onBlockPlace(BlockPlaceEvent event) {
    //
  }

  default void onUseItem(PlayerInteractEvent event) {
    //
  }
}
