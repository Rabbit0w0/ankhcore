package org.inksnow.ankh.core.world.storage;

import lombok.val;
import org.inksnow.ankh.core.api.storage.ChunkStorage;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.api.world.storage.StorageBackend;
import org.inksnow.ankh.core.common.entity.LocationEmbedded;
import org.inksnow.ankh.core.common.entity.WorldChunkEmbedded;
import org.inksnow.ankh.core.database.DatabaseService;
import org.inksnow.ankh.core.world.entity.StoredBlockEntity;

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
    try (val session = databaseService.sessionFactory().openSession()) {
      return session.createQuery("select E from stored_block E where E.location.chunk = :chunk", BlockStorageEntry.class)
        .setParameter("chunk", WorldChunkEmbedded.warp(chunkStorage))
        .list()
        .stream();
    }
  }

  @Override
  public void store(@Nonnull ChunkStorage chunkStorage, @Nonnull List<BlockStorageEntry> entries) {
    try (val session = databaseService.sessionFactory().openSession()) {
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
