package bot.inker.ankh.core.api.hologram;

import bot.inker.ankh.core.api.util.IBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface HologramTask {
  static Builder builder() {
    return HologramService.instance().builder();
  }

  void updateContent(HologramContent content);

  void delete();

  interface Builder extends IBuilder<Builder, HologramTask> {
    InnerContentBuilder content();

    Builder content(HologramContent content);

    Builder location(Location location);

    HologramTask build();
  }

  interface InnerContentBuilder extends IBuilder<InnerContentBuilder, Builder> {
    InnerContentBuilder appendContent(String content);

    InnerContentBuilder appendItem(ItemStack item);
  }
}
