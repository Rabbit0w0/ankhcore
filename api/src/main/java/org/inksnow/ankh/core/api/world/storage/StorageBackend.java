package org.inksnow.ankh.core.api.world.storage;

import org.inksnow.ankh.core.api.storage.ChunkStorage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

/**
 * StorageBackend store world data, likes block's pos and meta
 */
public interface StorageBackend {
  /**
   * Provide a stream include all ankh-blocks in WorldChunkEmbedded
   *
   * @param worldChunk world chunk which contains ankh-blocks
   * @return stream of entries
   */
  Stream<BlockStorageEntry> provide(@Nonnull ChunkStorage worldChunk);


  /**
   * Store ankh-blocks in WorldChunkEmbedded
   *
   * @param worldChunk world chunk which contains ankh-blocks
   * @param entries    ankh-blocks entry
   */
  void store(@Nonnull ChunkStorage worldChunk, @Nonnull List<BlockStorageEntry> entries);
}