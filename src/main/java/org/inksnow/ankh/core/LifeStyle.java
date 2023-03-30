package org.inksnow.ankh.core;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class LifeStyle {
  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private void onEnable() {
    val worldList = Bukkit.getWorlds();
    for (World world : worldList) {
      for (Chunk chunk : world.getLoadedChunks()) {
        if(chunk.isLoaded()){
          logger.error("====================================================");
          logger.error("AnkhCore is not designed to load after server running, It's very dangerous");
          logger.error("To protect your saves or data, AnkhCore will stop your server");
          logger.error("It's not a issue, please disable plugins likes PluginMan, YUM");
          logger.error("====================================================");
          Bukkit.shutdown();
          return;
        }
      }
    }
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
