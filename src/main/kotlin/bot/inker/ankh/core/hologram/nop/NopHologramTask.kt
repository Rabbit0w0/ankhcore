package bot.inker.ankh.core.hologram.nop

import bot.inker.ankh.core.api.hologram.HologramContent
import bot.inker.ankh.core.api.hologram.HologramTask
import bot.inker.ankh.core.common.dsl.ensurePrimaryThread
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class NopHologramTask : HologramTask {
  override fun updateContent(content: HologramContent) {
    //
  }

  override fun delete() {
    //
  }

  class Builder : HologramTask.Builder {
    override fun getThis() = this

    override fun build(): HologramTask {
      ensurePrimaryThread()
      return NopHologramTask()
    }

    override fun content() = InnerContentBuilder(this)
    override fun content(content: HologramContent) = this
    override fun location(location: Location) = this
  }

  class InnerContentBuilder(
    private val parent: Builder,
  ) : HologramTask.InnerContentBuilder {
    override fun getThis() = this
    override fun build() = parent
    override fun appendContent(content: String) = this
    override fun appendItem(item: ItemStack) = this
  }
}