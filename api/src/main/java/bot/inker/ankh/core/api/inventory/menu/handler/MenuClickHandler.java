package bot.inker.ankh.core.api.inventory.menu.handler;

import bot.inker.ankh.core.api.inventory.menu.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface MenuClickHandler {
  boolean onClick(Player p, int slot, ItemStack item, ClickAction action);
}