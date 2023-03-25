package bot.inker.ankh.core.api.hologram;

import bot.inker.ankh.core.api.util.IBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface HologramTask {

  void updateContent(@Nonnull HologramContent content);

  void delete();

  static @Nonnull Builder builder() {
    return HologramService.instance().builder();
  }

  interface Builder extends IBuilder<Builder, HologramTask> {
    @Nonnull InnerContentBuilder content();

    @Nonnull Builder content(@Nonnull HologramContent content);

    @Nonnull Builder location(@Nonnull Location location);
  }

  interface InnerContentBuilder extends IBuilder<InnerContentBuilder, Builder> {
    @Nonnull InnerContentBuilder appendContent(@Nonnull String content);

    @Nonnull InnerContentBuilder appendItem(@Nonnull ItemStack item);
  }
}
