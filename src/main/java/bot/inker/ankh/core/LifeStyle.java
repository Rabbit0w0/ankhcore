package bot.inker.ankh.core;

import bot.inker.ankh.core.api.plugin.PluginLifeCycle;
import bot.inker.ankh.core.api.plugin.annotations.SubscriptLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class LifeStyle {
  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private void onEnable() {

  }

  @SubscriptLifecycle(PluginLifeCycle.DISABLE)
  private void onDisable() {
    if (!Bukkit.getServer().isStopping()) {
      logger.error("====================================================");
      logger.error("AnkhCore is not designed to disable when server running, It's very dangerous");
      logger.error("To protect your saves or data, AnkhCore will stop your server");
      logger.error("It's not a issue, please disable plugins likes PluginMan, YUM");
      logger.error("====================================================");
      Bukkit.shutdown();
    }
  }
}
