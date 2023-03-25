package bot.inker.ankh.core.api.block;

import bot.inker.ankh.core.api.storage.LocationStorage;

import javax.annotation.Nonnull;

public interface AsyncTickableBlock extends AnkhBlock {
  void runAsyncTick(@Nonnull LocationStorage location);
}
