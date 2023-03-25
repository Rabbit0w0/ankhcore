package org.inksnow.ankh.core.hologram.nop;

import org.inksnow.ankh.core.api.hologram.HologramContent;
import org.inksnow.ankh.core.api.hologram.HologramService;
import org.inksnow.ankh.core.api.hologram.HologramTask;

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
