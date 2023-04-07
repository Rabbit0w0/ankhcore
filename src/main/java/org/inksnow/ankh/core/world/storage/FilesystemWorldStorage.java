package org.inksnow.ankh.core.world.storage;

import com.google.common.primitives.Longs;
import lombok.val;
import org.bukkit.Chunk;
import org.inksnow.ankh.core.common.entity.WorldChunkEmbedded;
import org.inksnow.ankh.core.common.util.HexUtil;
import org.inksnow.ankh.core.common.util.UUIDUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class FilesystemWorldStorage extends AbstractIoWorldStorage {
  private static final Path basePath = Paths.get("data-storage", "ankh-world-storage");

  private Path getChunkStoragePath(WorldChunkEmbedded worldChunk) {
    val chunkIdHex = HexUtil.toHex(Longs.toByteArray(worldChunk.chunkId()));
    return basePath.resolve(UUIDUtil.toPlainString(worldChunk.worldId()))
        .resolve(chunkIdHex + ".bin");
  }


  @Nullable
  @Override
  public InputStream openInputStream(@Nonnull Chunk chunk) {
    val worldChunk = WorldChunkEmbedded.of(chunk);
    val targetPath = getChunkStoragePath(worldChunk);
    if (!Files.exists(targetPath) || Files.isDirectory(targetPath)) {
      return null;
    }
    try {
      return Files.newInputStream(targetPath);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Nonnull
  @Override
  public OutputStream openOutputStream(@Nonnull Chunk chunk) {
    val worldChunk = WorldChunkEmbedded.of(chunk);
    val targetPath = getChunkStoragePath(worldChunk);
    try {
      Files.createDirectories(targetPath.getParent());
      return Files.newOutputStream(targetPath);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
