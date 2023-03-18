package bot.inker.ankh.testplugin;

import bot.inker.ankh.core.api.plugin.AnkhBukkitPlugin;
import bot.inker.ankh.core.api.plugin.AnkhPluginContainer;

public class TestBukkitPluginLoader extends AnkhBukkitPlugin {
  private static final AnkhPluginContainer container = AnkhBukkitPlugin.initial(TestBukkitPluginLoader.class);

  @Override
  protected AnkhPluginContainer getContainer() {
    return container;
  }
}
