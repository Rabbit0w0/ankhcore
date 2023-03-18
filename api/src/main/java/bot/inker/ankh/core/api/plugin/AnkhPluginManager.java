package bot.inker.ankh.core.api.plugin;

import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;

public interface AnkhPluginManager {
  AnkhPluginContainer register(Class<? extends AnkhBukkitPlugin> pluginClass, File file, ClassLoader classLoader, PluginDescriptionFile descriptionFile, AnkhPluginYml pluginYml);
}
