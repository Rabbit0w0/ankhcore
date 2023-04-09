package org.inksnow.ankh.groovy;

import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin;
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer;

public class AnkhGroovyPlugin extends AnkhBukkitPlugin {
  private static final AnkhPluginContainer container;

  static {
    container = initial(AnkhGroovyPlugin.class);
  }

  @Override
  protected AnkhPluginContainer getContainer() {
    return container;
  }
}
