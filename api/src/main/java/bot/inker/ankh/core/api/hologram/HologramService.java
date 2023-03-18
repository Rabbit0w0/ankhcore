package bot.inker.ankh.core.api.hologram;

import bot.inker.ankh.core.api.AnkhCore;

public interface HologramService {
  static HologramService instance() {
    return AnkhCore.getInstance(HologramService.class);
  }

  HologramContent.Builder content();

  HologramTask.Builder builder();
}
