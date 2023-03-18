package bot.inker.ankh.core.api.hologram;

import bot.inker.ankh.core.api.util.IBuilder;
import org.bukkit.inventory.ItemStack;

public interface HologramContent {
  static Builder builder() {
    return HologramService.instance().content();
  }

  interface Builder extends IBuilder<Builder, HologramContent> {
    Builder appendContent(String content);

    Builder appendItem(ItemStack item);
  }
}
