package bot.inker.ankh.core.api.world.storage;

import bot.inker.ankh.core.api.storage.ChunkStorage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

/**
 * StorageBackend store world data, likes block's pos and meta
 */
public interface StorageBackend {
   /**
    * Provide a stream include all ankh-blocks in WorldChunkEmbedded
    */
   Stream<BlockStorageEntry> provide(@Nonnull ChunkStorage worldChunk);

   /**
    * Store ankh-blocks in WorldChunkEmbedded
    */
   void store(@Nonnull ChunkStorage worldChunk, @Nonnull List<BlockStorageEntry> entries);
}