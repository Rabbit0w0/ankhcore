package org.inksnow.ankh.core.api.inventory.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

public enum ClickAction {
  LEFT(true, false, false),
  LEFT_SHIFT(true, false, true),
  RIGHT(false, true, false),
  RIGHT_SHIFT(false, true, true),
  NOTHING(false, false, false);

  private final boolean leftClicked;
  private final boolean rightClicked;
  private final boolean shiftClicked;

  ClickAction(boolean leftClicked, boolean rightClicked, boolean shiftClicked) {
    this.leftClicked = leftClicked;
    this.rightClicked = rightClicked;
    this.shiftClicked = shiftClicked;
  }

  public static ClickAction of(boolean leftClicked, boolean rightClicked, boolean shiftClicked) {
    if (leftClicked == rightClicked) {
      return NOTHING;
    } else if (leftClicked) {
      if (shiftClicked) {
        return LEFT_SHIFT;
      } else {
        return LEFT;
      }
    } else if (rightClicked) {
      if (shiftClicked) {
        return RIGHT_SHIFT;
      } else {
        return RIGHT;
      }
    }
    throw new IllegalStateException("Never happened");
  }

  public static ClickAction of(InventoryClickEvent event) {
    return of(event.isLeftClick(), event.isRightClick(), event.isShiftClick());
  }

  public boolean leftClicked() {
    return leftClicked;
  }

  public boolean rightClicked() {
    return rightClicked;
  }

  public boolean shiftClicked() {
    return shiftClicked;
  }
}