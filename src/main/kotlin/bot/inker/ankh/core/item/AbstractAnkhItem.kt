package bot.inker.ankh.core.item

import bot.inker.ankh.core.api.item.AnkhItem
import bot.inker.ankh.core.libs.nbtapi.NBTItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

abstract class AbstractAnkhItem : AnkhItem {
  abstract val itemId: NamespacedKey
  abstract val material: Material
  abstract val itemName: Component
  abstract val lores: List<Component>

  override fun getKey(): NamespacedKey = itemId

  fun createItem(): ItemStack {
    return ItemStack(Material.CLOCK).also(this::updateItem)
  }

  final override fun updateItem(item: ItemStack) {
    item.type = material

    NBTItem(item).apply {
      if (itemId.asString() != getString("ankh-core:item-id")) {
        setString("ankh-core:item-id", itemId.asString())
      }
      onUpdateItemNbt(this)
    }.applyNBT(item)

    item.editMeta { meta ->
      meta.displayName(itemName)
      meta.lore(lores)
      onUpdateItemMeta(meta)
    }

    onUpdateItem(item)
  }

  open fun onUpdateItem(item: ItemStack) {
    //
  }

  open fun onUpdateItemNbt(nbtItem: NBTItem) {
    //
  }

  open fun onUpdateItemMeta(itemMeta: ItemMeta) {
    //
  }
}