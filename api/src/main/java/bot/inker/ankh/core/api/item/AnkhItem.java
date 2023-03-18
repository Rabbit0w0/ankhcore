package bot.inker.ankh.core.api.item;

import org.bukkit.Keyed;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface AnkhItem extends Keyed {
  void updateItem(ItemStack item);
  void onBlockPlace(BlockPlaceEvent event);
  void onUseItem(PlayerInteractEvent event);
}
