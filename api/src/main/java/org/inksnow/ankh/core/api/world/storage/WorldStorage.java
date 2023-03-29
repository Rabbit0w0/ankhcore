package org.inksnow.ankh.core.api.world.storage;

import org.bukkit.Chunk;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * StorageBackend store world data, likes block's pos and meta
 */
public interface WorldStorage {
  /**
   * Provide a stream include all ankh-blocks in WorldChunkEmbedded
   *
   * @param chunk chunk which contains ankh-blocks
   * @return list of entries
   */
  List<BlockStorageEntry> provide(@Nonnull Chunk chunk);


  /**
   * Store ankh-blocks in WorldChunkEmbedded
   *
   * @param chunk chunk which contains ankh-blocks
   * @param entries    ankh-blocks entry
   */
  void store(@Nonnull Chunk chunk, @Nonnull List<BlockStorageEntry> entries);
}