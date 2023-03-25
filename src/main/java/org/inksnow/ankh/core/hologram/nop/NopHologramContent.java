package org.inksnow.ankh.core.hologram.nop;

import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.hologram.HologramContent;

import javax.annotation.Nonnull;

public class NopHologramContent implements HologramContent {
  public static class Builder implements HologramContent.Builder {
    @Override
    public Builder appendContent(String content) {
      return this;
    }

    @Override
    public Builder appendItem(ItemStack item) {
      return this;
    }

    @Override
    public @Nonnull Builder getThis() {
      return this;
    }

    @Override
    public @Nonnull HologramContent build() {
      return new NopHologramContent();
    }
  }
}
