package org.inksnow.ankh.core.world.storage;

import lombok.val;
import org.bukkit.Chunk;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.api.world.storage.WorldStorage;
import org.inksnow.ankh.core.common.entity.LocationEmbedded;
import org.inksnow.ankh.core.common.entity.WorldChunkEmbedded;
import org.inksnow.ankh.core.database.DatabaseService;
import org.inksnow.ankh.core.world.entity.StoredBlockEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class DatabaseWorldStorage implements WorldStorage {
  private final DatabaseService databaseService;

  @Inject
  private DatabaseWorldStorage(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @Override
  public @Nonnull List<BlockStorageEntry> provide(@Nonnull Chunk chunk) {
    try (val session = databaseService.sessionFactory().openSession()) {
      return session.createQuery("select E from stored_block E where E.location.chunk = :chunk", BlockStorageEntry.class)
          .setParameter("chunk", WorldChunkEmbedded.of(chunk))
          .list();
    }
  }

  @Override
  public void store(@Nonnull Chunk chunk, @Nonnull List<BlockStorageEntry> entries) {
    try (val session = databaseService.sessionFactory().openSession()) {
      session.beginTransaction();

      session.createQuery("delete from stored_block E where E.location.chunk = :chunk", null)
          .setParameter("chunk", WorldChunkEmbedded.of(chunk))
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
