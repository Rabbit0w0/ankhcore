package bot.inker.ankh.core.world.storage

import bot.inker.ankh.core.common.entity.LocationEmbedded
import org.bukkit.NamespacedKey

interface BlockStorageEntry {
  fun location(): LocationEmbedded
  fun blockId(): NamespacedKey
  fun content(): ByteArray

  companion object {
    operator fun invoke(
      location: LocationEmbedded,
      blockId: NamespacedKey,
      content: ByteArray,
    ) = object : BlockStorageEntry {
      override fun location() = location
      override fun blockId() = blockId
      override fun content() = content
    }
  }
}