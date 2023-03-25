package org.inksnow.ankh.core.hologram.hds

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI
import org.inksnow.ankh.core.api.AnkhCoreLoader
import org.inksnow.ankh.core.api.hologram.HologramService
import org.inksnow.ankh.core.api.hologram.HologramTask
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