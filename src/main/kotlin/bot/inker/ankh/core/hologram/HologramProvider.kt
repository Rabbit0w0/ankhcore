package bot.inker.ankh.core.hologram

import bot.inker.ankh.core.api.hologram.HologramService
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.hologram.hds.HdsHologramService
import bot.inker.ankh.core.hologram.nop.NopHologramService
import com.google.inject.Injector
import org.bukkit.Bukkit
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class HologramProvider @Inject private constructor(
  private val injector: Injector,
) : Provider<HologramService> {
  private val logger by logger()
  private val delegate by lazy {
    if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
       return@lazy injector.getInstance(HdsHologramService::class.java)
    }
    logger.info("====================================================")
    logger.info("No support hologram plugin found, all hologram will not display")
    logger.info("We recommend bukkit plugin 'HolographicDisplays' to support it")
    logger.info("You can download it from https://dev.bukkit.org/projects/holographic-displays")
    logger.info("====================================================")
    return@lazy injector.getInstance(NopHologramService::class.java)
  }
  override fun get(): HologramService = delegate
}