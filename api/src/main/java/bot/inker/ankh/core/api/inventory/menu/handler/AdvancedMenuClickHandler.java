package bot.inker.ankh.core.api.inventory.menu.handler;

import bot.inker.ankh.core.api.inventory.menu.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface AdvancedMenuClickHandler extends MenuClickHandler {
  boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action);
}