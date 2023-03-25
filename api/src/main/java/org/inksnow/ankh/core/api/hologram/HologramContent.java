package org.inksnow.ankh.core.api.hologram;

import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.util.IBuilder;

public interface HologramContent {
  static Builder builder() {
    return HologramService.instance().content();
  }

  interface Builder extends IBuilder<Builder, HologramContent> {
    Builder appendContent(String content);

    Builder appendItem(ItemStack item);
  }
}
