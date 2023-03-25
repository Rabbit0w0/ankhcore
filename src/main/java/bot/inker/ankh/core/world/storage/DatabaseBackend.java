package bot.inker.ankh.core.world.storage;

import bot.inker.ankh.core.api.storage.ChunkStorage;
import bot.inker.ankh.core.api.world.storage.BlockStorageEntry;
import bot.inker.ankh.core.api.world.storage.StorageBackend;
import bot.inker.ankh.core.common.entity.LocationEmbedded;
import bot.inker.ankh.core.common.entity.WorldChunkEmbedded;
import bot.inker.ankh.core.database.DatabaseService;
import bot.inker.ankh.core.world.entity.StoredBlockEntity;
import lombok.val;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class DatabaseBackend implements StorageBackend {
  private final DatabaseService databaseService;

  @Inject
  private DatabaseBackend(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @Override
  public @Nonnull Stream<BlockStorageEntry> provide(@Nonnull ChunkStorage chunkStorage) {
    try (val session = databaseService.getSessionFactory().openSession()) {
      return session.createQuery("select E from stored_block E where E.location.chunk = :chunk", BlockStorageEntry.class)
        .setParameter("chunk", WorldChunkEmbedded.warp(chunkStorage))
        .list()
        .stream();
    }
  }

  @Override
  public void store(@Nonnull ChunkStorage chunkStorage, @Nonnull List<BlockStorageEntry> entries) {
    try (val session = databaseService.getSessionFactory().openSession()) {
      session.beginTransaction();

      session.createQuery("delete from stored_block E where E.location.chunk = :chunk", null)
        .setParameter("chunk", chunkStorage)
        .executeUpdate();
      for (val entry : entries) {
        val entity = new StoredBlockEntity(
          LocationEmbedded.warp(entry.location()),
          entry.blockId(),
          entry.content()
        );
        session.persist(entity);
      }

      session.getTransaction().commit();
    }
  }
}
