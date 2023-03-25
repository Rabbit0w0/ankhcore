package org.inksnow.ankh.core.api.inventory.menu.handler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.inventory.menu.ClickAction;

@FunctionalInterface
public interface MenuClickHandler {
  boolean onClick(Player p, int slot, ItemStack item, ClickAction action);
}