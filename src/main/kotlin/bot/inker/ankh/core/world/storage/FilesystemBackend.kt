package bot.inker.ankh.core.world.storage

import bot.inker.ankh.core.api.AnkhCoreLoader
import bot.inker.ankh.core.api.storage.ChunkStorage
import bot.inker.ankh.core.api.world.storage.BlockStorageEntry
import bot.inker.ankh.core.api.world.storage.StorageBackend
import bot.inker.ankh.core.common.entity.LocationEmbedded
import bot.inker.ankh.core.common.entity.WorldChunkEmbedded
import bot.inker.ankh.core.common.util.HexUtil
import bot.inker.ankh.core.common.util.UUIDUtil
import com.google.common.primitives.Longs
import net.kyori.adventure.key.Key
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream
import javax.inject.Inject

class FilesystemBackend @Inject private constructor(
  private val ankhCoreLoader: AnkhCoreLoader,
) : StorageBackend {
  private val basePath = Paths.get("data-storage", "ankh-world-storage")

  private fun getChunkStoragePath(worldChunk: WorldChunkEmbedded): Path {
    val chunkIdHex = HexUtil.toHex(Longs.toByteArray(worldChunk.chunkId()))
    return basePath.resolve(UUIDUtil.toPlainString(worldChunk.worldId()))
      .resolve("$chunkIdHex.bin")
  }

  override fun provide(chunkStorage: ChunkStorage): Stream<BlockStorageEntry> {
    val worldChunk = WorldChunkEmbedded.warp(chunkStorage)
    val targetPath = getChunkStoragePath(worldChunk)
    if (!Files.exists(targetPath) || Files.isDirectory(targetPath)) {
      return Stream.empty()
    }
    Files.newInputStream(targetPath).let(::DataInputStream).use { input ->
      val count = input.readInt()
      val result = ArrayList<BlockStorageEntry>(count)
      for (i in 0 until count) {
        BlockStorageEntry.of(
          LocationEmbedded.of(
            worldChunk,
            input.readLong()
          ),
          ByteArray(input.readInt())
            .also(input::readFully)
            .toString(StandardCharsets.UTF_8)
            .let(Key::key),
          ByteArray(input.readInt()).also(input::readFully),
        ).let(result::add)
      }
      return result.stream()
    }
  }

  override fun store(chunkStorage: ChunkStorage, entries: List<BlockStorageEntry>) {
    val worldChunk = WorldChunkEmbedded.warp(chunkStorage)
    val targetPath = getChunkStoragePath(worldChunk)

    if (entries.isEmpty()) {
      Files.deleteIfExists(targetPath)
      return
    } else {
      Files.createDirectories(targetPath.parent)
    }

    val tempPath = Files.createTempFile(targetPath.parent, "ankh_", ".bin.tmp")

    Files.newOutputStream(tempPath).let(::DataOutputStream).use { output ->
      output.writeInt(entries.size)
      entries.forEach { entry ->
        val location = LocationEmbedded.warp(entry.location())
        output.writeLong(location.position())
        val blockIdBytes = entry.blockId().asString().toByteArray(StandardCharsets.UTF_8)
        output.writeInt(blockIdBytes.size)
        output.write(blockIdBytes)
        output.writeInt(entry.content().size)
        output.write(entry.content())
      }
    }
    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
  }
}