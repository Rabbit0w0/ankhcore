package org.inksnow.ankh.core.api.world;

import org.bukkit.Location;
import org.inksnow.ankh.core.api.block.AnkhBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WorldService {
  @Nullable
  AnkhBlock getBlock(@Nonnull Location location);

  void setBlock(@Nonnull Location location, @Nonnull AnkhBlock ankhBlock);

  void removeBlock(@Nonnull Location location);
}
