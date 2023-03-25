package org.inksnow.ankh.core.hologram.nop;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.hologram.HologramContent;
import org.inksnow.ankh.core.api.hologram.HologramTask;
import org.jetbrains.annotations.NotNull;

public class NopHologramTask implements HologramTask {
  @Override
  public void updateContent(@NotNull HologramContent content) {

  }

  @Override
  public void delete() {

  }

  public static class Builder implements HologramTask.Builder {

    @NotNull
    @Override
    public InnerContentBuilder content() {
      return new InnerContentBuilder(this);
    }

    @NotNull
    @Override
    public Builder content(@NotNull HologramContent content) {
      return this;
    }

    @NotNull
    @Override
    public Builder location(@NotNull Location location) {
      return this;
    }

    @NotNull
    @Override
    public Builder getThis() {
      return this;
    }

    @NotNull
    @Override
    public HologramTask build() {
      return new NopHologramTask();
    }
  }

  public static class InnerContentBuilder implements HologramTask.InnerContentBuilder {
    private final Builder parent;

    public InnerContentBuilder(Builder parent) {
      this.parent = parent;
    }

    @NotNull
    @Override
    public InnerContentBuilder appendContent(@NotNull String content) {
      return this;
    }

    @NotNull
    @Override
    public InnerContentBuilder appendItem(@NotNull ItemStack item) {
      return this;
    }

    @NotNull
    @Override
    public InnerContentBuilder getThis() {
      return this;
    }

    @NotNull
    @Override
    public Builder build() {
      return parent;
    }
  }
}
