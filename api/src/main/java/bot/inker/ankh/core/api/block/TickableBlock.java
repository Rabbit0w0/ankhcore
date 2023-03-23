package bot.inker.ankh.core.api.block;

import bot.inker.ankh.core.api.entity.LocationStorage;

import javax.annotation.Nonnull;

public interface TickableBlock extends AnkhBlock {
  void runTick(@Nonnull LocationStorage location);
}
