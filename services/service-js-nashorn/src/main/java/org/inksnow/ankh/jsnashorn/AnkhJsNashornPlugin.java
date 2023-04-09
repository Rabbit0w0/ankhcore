package org.inksnow.ankh.jsnashorn;

import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin;
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer;

public class AnkhJsNashornPlugin extends AnkhBukkitPlugin {
  private static final AnkhPluginContainer container;

  static {
    container = initial(AnkhJsNashornPlugin.class);
  }

  @Override
  protected AnkhPluginContainer getContainer() {
    return container;
  }
}
