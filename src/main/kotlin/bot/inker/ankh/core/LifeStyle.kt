package bot.inker.ankh.core

import bot.inker.ankh.core.api.plugin.PluginLifeCycle
import bot.inker.ankh.core.api.plugin.annotations.SubscriptLifecycle
import bot.inker.ankh.core.common.dsl.logger
import org.bukkit.Bukkit
import javax.inject.Singleton

@Singleton
class LifeStyle {
  private val logger by logger()

  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private fun onEnable(){
    //
  }

  @SubscriptLifecycle(PluginLifeCycle.DISABLE)
  private fun onDisable(){
    if (!Bukkit.getServer().isStopping) {
      logger.error("====================================================")
      logger.error("AnkhCore is not designed to disable when server running, It's very dangerous")
      logger.error("To protect your saves or data, AnkhCore will stop your server")
      logger.error("It's not a issue, please disable plugins likes PluginMan, YUM")
      logger.error("====================================================")
      Bukkit.shutdown()
    }
  }
}