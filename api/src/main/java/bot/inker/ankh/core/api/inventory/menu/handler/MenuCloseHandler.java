package bot.inker.ankh.core.api.inventory.menu.handler;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface MenuCloseHandler {
  void onClose(Player p);
}