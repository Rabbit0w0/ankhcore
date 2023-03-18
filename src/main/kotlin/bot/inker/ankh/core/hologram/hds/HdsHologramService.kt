package bot.inker.ankh.core.hologram.hds

import bot.inker.ankh.core.api.AnkhCoreLoader
import bot.inker.ankh.core.api.hologram.HologramService
import bot.inker.ankh.core.api.hologram.HologramTask
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HdsHologramService @Inject private constructor(
  private val ankhCoreLoader: AnkhCoreLoader,
) : HologramService {
  private val hdApi by lazy {
    HolographicDisplaysAPI.get(ankhCoreLoader)
  }

  override fun content() = HdsHologramContent.Builder()
  override fun builder(): HologramTask.Builder {
    return HdsHologramTask.Builder(hdApi)
  }
}