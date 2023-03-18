package bot.inker.ankh.core.hologram.nop

import bot.inker.ankh.core.api.hologram.HologramContent
import org.bukkit.inventory.ItemStack

class NopHologramContent : HologramContent {
  class Builder : HologramContent.Builder {
    override fun getThis() = this
    override fun build() = NopHologramContent()
    override fun appendContent(content: String) = this
    override fun appendItem(item: ItemStack) = this
  }
}