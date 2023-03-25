package org.inksnow.ankh.core.api.hologram;

import org.inksnow.ankh.core.api.AnkhCore;

public interface HologramService {
  static HologramService instance() {
    return AnkhCore.getInstance(HologramService.class);
  }

  HologramContent.Builder content();

  HologramTask.Builder builder();
}
