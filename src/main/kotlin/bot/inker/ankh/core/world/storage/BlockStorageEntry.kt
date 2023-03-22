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
    ):BlockStorageEntry = Default(location, blockId, content)
  }

  private class Default(
    private val location: LocationEmbedded,
    private val blockId: NamespacedKey,
    private val content: ByteArray
  ):BlockStorageEntry{
    override fun location(): LocationEmbedded = location

    override fun blockId(): NamespacedKey = blockId

    override fun content(): ByteArray = content
    override fun toString(): String {
      return "BlockStorageEntry(location=$location, blockId=$blockId, content=${content.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Default

      if (location != other.location) return false
      if (blockId != other.blockId) return false
      if (!content.contentEquals(other.content)) return false

      return true
    }

    override fun hashCode(): Int {
      var result = location.hashCode()
      result = 31 * result + blockId.hashCode()
      result = 31 * result + content.contentHashCode()
      return result
    }
  }
}