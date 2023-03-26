package org.inksnow.ankh.core.api.inventory.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryMenu extends InventoryHolder {
  default void acceptCloseEvent(InventoryCloseEvent event) {
    //
  }

  default void acceptClickEvent(InventoryClickEvent event) {
    //
  }

  default void acceptDragEvent(InventoryDragEvent event) {
    //
  }
}
