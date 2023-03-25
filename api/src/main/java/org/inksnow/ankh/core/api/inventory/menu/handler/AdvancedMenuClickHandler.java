package org.inksnow.ankh.core.api.inventory.menu.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.inventory.menu.ClickAction;

public interface AdvancedMenuClickHandler extends MenuClickHandler {
  boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action);
}