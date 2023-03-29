package org.inksnow.ankh.core.inventory.menu;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.inksnow.ankh.core.api.inventory.menu.ClickAction;
import org.inksnow.ankh.core.api.inventory.menu.InventoryMenu;
import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.ioc.IocLazy;
import org.inksnow.ankh.core.common.util.CheckUtil;

import javax.annotation.Nonnull;

@Slf4j
public class AbstractChestMenu implements InventoryMenu {
  private static final DcLazy<AnkhMenuService> menuService = IocLazy.of(AnkhMenuService.class);
  private final boolean[] modifiableSlots = new boolean[54];
  @Getter
  @Setter
  private Component title = createTitle();
  @Getter
  private final Inventory inventory = createInventory();
  @Getter
  @Setter
  private boolean modifiable = true;
  @Getter
  @Setter
  private boolean playerModifiable = false;

  protected static AnkhMenuService menuService() {
    return menuService.get();
  }

  protected Component createTitle() {
    return Component.text("ankh chest menu");
  }

  protected Inventory createInventory() {
    return Bukkit.createInventory(this, 54, title);
  }

  protected void acceptMenuOpen(Player player, InventoryView view) {
    //
  }

  protected void acceptMenuClose(Player player, InventoryView view) {
    //
  }

  public AbstractChestMenu modifiableSlot(int slot, boolean newValue) {
    modifiableSlots[slot] = newValue;
    return this;
  }

  public boolean modifiableSlot(int slot) {
    if (slot >= 54) {
      throw new IllegalArgumentException("Invalid slot id: " + slot);
    }
    return modifiableSlots[slot];
  }

  public void openForPlayer(Player... players) {
    CheckUtil.ensureMainThread();
    for (Player player : players) {
      acceptMenuOpen(player, player.openInventory(inventory));
    }
  }

  @Override
  public final void acceptCloseEvent(InventoryCloseEvent event) {
    val player = (Player) event.getPlayer();
    acceptMenuClose(player, event.getView());
  }

  @Override
  public final void acceptClickEvent(InventoryClickEvent event) {
    val player = (Player) event.getWhoClicked();
    val clickAction = ClickAction.of(event);
    val isClickChest = event.getRawSlot() < event.getInventory().getSize();
    var cancelled = false;

    switch (event.getAction()) {
      case NOTHING: {

        break;
      }
      case PICKUP_ALL: {

        break;
      }
      case PICKUP_SOME: {

        break;
      }
      case PICKUP_HALF: {

        break;
      }
      case PICKUP_ONE: {

        break;
      }
      case PLACE_ALL: {

        break;
      }
      case PLACE_SOME: {

        break;
      }
      case PLACE_ONE: {

        break;
      }
      case SWAP_WITH_CURSOR: {

        break;
      }
      case DROP_ALL_CURSOR: {

        break;
      }
      case DROP_ONE_CURSOR: {

        break;
      }
      case DROP_ALL_SLOT: {

        break;
      }
      case DROP_ONE_SLOT: {

        break;
      }
      case MOVE_TO_OTHER_INVENTORY: {
        if (!modifiable || !playerModifiable) {
          cancelled = true;
        }
        break;
      }
      case HOTBAR_MOVE_AND_READD: {
        if (!playerModifiable) {
          cancelled = true;
        }
        break;
      }
      case HOTBAR_SWAP: {
        if (!playerModifiable) {
          cancelled = true;
        }
        break;
      }
      case CLONE_STACK: {

        break;
      }
      case COLLECT_TO_CURSOR: {

        break;
      }
      case UNKNOWN: {

        break;
      }
    }

    if (isClickChest) {
      if (modifiable) {
        cancelled = !modifiableSlot(event.getRawSlot());
      } else {
        cancelled = true;
      }
    } else if (!playerModifiable) {
      cancelled = true;
    }

    if (cancelled) {
      event.setCancelled(true);
    }
  }

  @Override
  public final void acceptDragEvent(InventoryDragEvent event) {
    val player = (Player) event.getWhoClicked();
    var cancelled = false;
    for (Integer rawSlot : event.getRawSlots()) {
      val isClickChest = rawSlot < event.getInventory().getSize();
      if (isClickChest) {
        if (modifiable) {
          cancelled = !modifiableSlot(rawSlot);
        } else {
          cancelled = true;
        }
      } else if (!playerModifiable) {
        cancelled = true;
      }
    }
    if (cancelled) {
      event.setCancelled(true);
    }
  }

  @Override
  public @Nonnull Inventory getInventory() {
    return inventory;
  }
}
