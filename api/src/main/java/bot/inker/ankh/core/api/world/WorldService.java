package bot.inker.ankh.core.api.world;

import bot.inker.ankh.core.api.block.AnkhBlock;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WorldService {
  @Nullable
  AnkhBlock getBlock(@Nonnull Location location);

  void setBlock(@Nonnull Location location, @Nonnull AnkhBlock ankhBlock);

  void removeBlock(@Nonnull Location location);
}
