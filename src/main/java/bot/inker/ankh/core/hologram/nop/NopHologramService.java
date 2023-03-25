package bot.inker.ankh.core.hologram.nop;

import bot.inker.ankh.core.api.hologram.HologramContent;
import bot.inker.ankh.core.api.hologram.HologramService;
import bot.inker.ankh.core.api.hologram.HologramTask;

import javax.inject.Inject;

public class NopHologramService implements HologramService {
  @Inject
  private NopHologramService() {

  }

  @Override
  public HologramContent.Builder content() {
    return new NopHologramContent.Builder();
  }

  @Override
  public HologramTask.Builder builder() {
    return null;
  }
}
