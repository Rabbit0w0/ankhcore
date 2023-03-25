package bot.inker.ankh.core.api.inventory.menu.handler;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface MenuOpeningHandler {
  void onOpen(Player p);
}