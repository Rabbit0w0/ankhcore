package bot.inker.ankh.core.inventory.menu;

import bot.inker.ankh.core.api.inventory.menu.InventoryMenu;
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class AnkhMenuService {
  private final Map<Player, InventoryMenu> menus = new WeakHashMap<>();

  @Inject
  private AnkhMenuService() {

  }

  @SubscriptEvent
  private void onInventoryClose(InventoryCloseEvent event) {
    val humanEntity = event.getPlayer();
    if (!(event.getPlayer() instanceof Player)) {
      return;
    }
    val player = (Player) humanEntity;
    val menu = menus.remove(player);
    if (menu != null) {
      menu.acceptCloseEvent(event);
    }
  }

  @SubscriptEvent(ignoreCancelled = true)
  private void onInventoryClick(InventoryClickEvent event) {
    val humanEntity = event.getWhoClicked();
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    val player = (Player) humanEntity;
    val menu = menus.get(player);
    if (menu != null) {
      player.sendMessage(event.getAction().name());
      menu.acceptClickEvent(event);
    }
  }

  @SubscriptEvent(ignoreCancelled = true)
  private void onInventoryDrag(InventoryDragEvent event) {
    event.getWhoClicked().sendMessage("DRAG");

    val humanEntity = event.getWhoClicked();
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    val player = (Player) humanEntity;
    val menu = menus.get(player);
    if (menu != null) {
      menu.acceptDragEvent(event);
    }
  }

  public void registerMenu(Player player, InventoryMenu chestMenu) {
    menus.put(player, chestMenu);
  }
}
