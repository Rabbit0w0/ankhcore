package bot.inker.ankh.loader;

import bot.inker.ankh.core.api.AnkhCoreLoader;
import bot.inker.ankh.core.api.plugin.AnkhBukkitPlugin;
import bot.inker.ankh.core.api.plugin.AnkhPluginContainer;
import bot.inker.ankh.loader.internal.AnkhBukkitPluginInternal;

public class AnkhCoreLoaderPlugin extends AnkhBukkitPlugin implements AnkhCoreLoader {
  public static final AnkhPluginContainer container;

  static {
    AnkhLoggerLoader.initial(AnkhCoreLoaderPlugin.class.getClassLoader());
    AnkhBukkitPluginInternal.ensureLoaded();
    container = AnkhBukkitPlugin.initial(AnkhCoreLoaderPlugin.class);
  }

  @Override
  public AnkhPluginContainer getContainer() {
    return container;
  }
}
