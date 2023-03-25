package org.inksnow.ankh.core.api.block;

import org.inksnow.ankh.core.api.storage.LocationStorage;

import javax.annotation.Nonnull;

public interface AsyncTickableBlock extends AnkhBlock {
  void runAsyncTick(@Nonnull LocationStorage location);
}
