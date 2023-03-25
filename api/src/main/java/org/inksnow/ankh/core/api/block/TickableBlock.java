package org.inksnow.ankh.core.api.block;

import org.inksnow.ankh.core.api.storage.LocationStorage;

import javax.annotation.Nonnull;

public interface TickableBlock extends AnkhBlock {
  void runTick(@Nonnull LocationStorage location);
}
