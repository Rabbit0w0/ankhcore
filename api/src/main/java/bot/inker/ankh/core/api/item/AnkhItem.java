package bot.inker.ankh.core.api.item;

import bot.inker.ankh.core.api.AnkhCore;
import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
