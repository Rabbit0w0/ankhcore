package bot.inker.ankh.core.hologram.nop

import bot.inker.ankh.core.api.hologram.HologramService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NopHologramService @Inject private constructor(

) : HologramService {
  override fun content() = NopHologramContent.Builder()
  override fun builder() = NopHologramTask.Builder()
}