package bot.inker.ankh.core.hologram.nop;

import bot.inker.ankh.core.api.hologram.HologramContent;
import org.bukkit.inventory.ItemStack;

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
