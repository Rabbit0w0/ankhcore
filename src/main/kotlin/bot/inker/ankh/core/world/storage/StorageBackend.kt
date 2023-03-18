package bot.inker.ankh.core.world.storage

import bot.inker.ankh.core.common.entity.WorldChunkEmbedded
import java.util.stream.Stream

/**
 * StorageBackend store world data, likes block's pos and meta
 */
interface StorageBackend {
  /**
   * Provide a stream include all ankh-blocks in WorldChunkEmbedded
   */
  fun provide(worldChunk: WorldChunkEmbedded): Stream<BlockStorageEntry>

  /**
   * Store ankh-blocks in WorldChunkEmbedded
   */
  fun store(worldChunk: WorldChunkEmbedded, entries: List<BlockStorageEntry>)
}