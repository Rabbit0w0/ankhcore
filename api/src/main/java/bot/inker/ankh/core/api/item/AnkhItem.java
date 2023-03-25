package bot.inker.ankh.core.api.item;

import net.kyori.adventure.key.Keyed;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface AnkhItem extends Keyed {
  String ITEM_ID_TAG = "ankh-core:item-id";

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
