package bot.inker.ankh.core.api.block;

import bot.inker.ankh.core.api.storage.LocationStorage;

import javax.annotation.Nonnull;

public interface TickableBlock extends AnkhBlock {
  void runTick(@Nonnull LocationStorage location);
}
