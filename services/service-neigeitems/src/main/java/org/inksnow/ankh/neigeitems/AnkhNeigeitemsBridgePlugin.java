package org.inksnow.ankh.neigeitems;

import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin;
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer;

public class AnkhNeigeitemsBridgePlugin extends AnkhBukkitPlugin {
  private static final AnkhPluginContainer container;

  static {
    container = initial(AnkhNeigeitemsBridgePlugin.class);
  }

  @Override
  protected AnkhPluginContainer getContainer() {
    return container;
  }
}
