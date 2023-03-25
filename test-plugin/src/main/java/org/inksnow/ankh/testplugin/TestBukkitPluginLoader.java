package org.inksnow.ankh.testplugin;

import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin;
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer;

public class TestBukkitPluginLoader extends AnkhBukkitPlugin {
  private static final AnkhPluginContainer container = AnkhBukkitPlugin.initial(TestBukkitPluginLoader.class);

  @Override
  protected AnkhPluginContainer getContainer() {
    return container;
  }
}
