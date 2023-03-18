package bot.inker.ankh.core.world.storage

import bot.inker.ankh.core.common.entity.WorldChunkEmbedded
import bot.inker.ankh.core.database.DatabaseService
import bot.inker.ankh.core.world.entity.StoredBlockEntity
import java.util.stream.Stream
import javax.inject.Inject

class DatabaseBackend @Inject private constructor(
  private val databaseService: DatabaseService,
) : StorageBackend {
  override fun provide(worldChunk: WorldChunkEmbedded): Stream<BlockStorageEntry> {
    var result: Stream<BlockStorageEntry> = Stream.empty()
    databaseService.sessionFactory.inSession { session ->
      result = session.createQuery(
        "select E from stored_block E where E.location.chunk = :chunk",
        StoredBlockEntity::class.java
      )
        .setParameter("chunk", worldChunk)
        .list()
        .stream()
        .map { it as BlockStorageEntry }
    }
    return result
  }

  override fun store(worldChunk: WorldChunkEmbedded, entries: List<BlockStorageEntry>) {
    databaseService.sessionFactory.inTransaction { session ->
      session.createQuery<Unit>("delete from stored_block E where E.location.chunk = :chunk", null)
        .setParameter("chunk", worldChunk)
        .executeUpdate()
      entries.map {
        StoredBlockEntity().apply {
          this.location = it.location()
          this.blockId = it.blockId().asString()
          this.content = it.content()
        }
      }.forEach(session::persist)
    }
  }
}