package org.inksnow.ankh.core.item.debug;

import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhCore;
import org.inksnow.ankh.core.api.inventory.menu.InventoryMenu;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DebugToolsMenu implements InventoryMenu {
  private static final Component MENU_TITLE = Component.text()
      .append(AnkhCore.PLUGIN_NAME_COMPONENT)
      .append(Component.text("debug tools", NamedTextColor.RED))
      .build();
  private final Inventory inventory;
  private final ItemStack[] defaultItems;

  @Inject
  private DebugToolsMenu(DebugRemoveItem debugRemoveItem) {
    inventory = Bukkit.createInventory(this, 54, MENU_TITLE);
    defaultItems = new ItemStack[54];
    defaultItems[0] = debugRemoveItem.createItem();
    fillItems();
  }

  @Override
  public @Nonnull Inventory getInventory() {
    return inventory;
  }

  @Override
  public void acceptDragEvent(InventoryDragEvent event) {
    fillItems();
  }

  @Override
  public void acceptClickEvent(InventoryClickEvent event) {
    fillItems();
  }

  private void fillItems(){
    for (int i = 0; i < 54; i++) {
      val defaultItem = defaultItems[i];
      inventory.setItem(i, defaultItem == null ? null : defaultItem.clone());
    }
  }
}
