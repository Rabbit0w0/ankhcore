package org.inksnow.ankh.core.inventory.menu;

import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.inksnow.ankh.core.api.inventory.menu.InventoryMenu;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnkhMenuService {

  @Inject
  private AnkhMenuService() {

  }

  @SubscriptEvent
  private void onInventoryClose(InventoryCloseEvent event) {
    val holder = event.getInventory().getHolder();
    if (!(holder instanceof InventoryMenu)) {
      return;
    }
    if (!(event.getPlayer() instanceof Player)) {
      return;
    }
    val menu = (InventoryMenu) holder;
    menu.acceptCloseEvent(event);
  }

  @SubscriptEvent(ignoreCancelled = true)
  private void onInventoryClick(InventoryClickEvent event) {
    val holder = event.getInventory().getHolder();
    if (!(holder instanceof InventoryMenu)) {
      return;
    }
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    val menu = (InventoryMenu) holder;
    menu.acceptClickEvent(event);
  }

  @SubscriptEvent(ignoreCancelled = true)
  private void onInventoryDrag(InventoryDragEvent event) {
    val holder = event.getInventory().getHolder();
    if (!(holder instanceof InventoryMenu)) {
      return;
    }
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    val menu = (InventoryMenu) holder;
    menu.acceptDragEvent(event);
  }
}
