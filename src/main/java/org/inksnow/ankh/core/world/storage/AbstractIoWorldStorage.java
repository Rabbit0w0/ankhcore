package org.inksnow.ankh.core.world.storage;

import lombok.val;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.api.world.storage.WorldStorage;
import org.inksnow.ankh.core.common.entity.LocationEmbedded;
import org.inksnow.ankh.core.common.entity.WorldChunkEmbedded;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractIoWorldStorage implements WorldStorage {
  public abstract @Nullable InputStream openInputStream(@Nonnull Chunk chunk);

  public abstract @Nonnull OutputStream openOutputStream(@Nonnull Chunk chunk);

  @Override
  public final List<BlockStorageEntry> provide(@Nonnull Chunk chunk) {
    val chunkEmbedded = WorldChunkEmbedded.of(chunk);
    val input = openInputStream(chunk);
    if (input == null) {
      return Collections.emptyList();
    }
    try (val in = new DataInputStream(input)) {
      val entryCount = in.readInt();
      val entryList = new ArrayList<BlockStorageEntry>(entryCount);
      for (int i = 0; i < entryCount; i++) {
        val blockId = in.readLong();
        val key = in.readUTF();
        val fullData = new byte[in.readInt()];
        in.readFully(fullData);
        entryList.add(BlockStorageEntry.of(
          LocationEmbedded.of(chunkEmbedded, blockId),
          Key.key(key),
          fullData
        ));
      }
      return entryList;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public final void store(@Nonnull Chunk chunk, @Nonnull List<BlockStorageEntry> entries) {
    try (val out = new DataOutputStream(openOutputStream(chunk))) {
      out.writeInt(entries.size());
      for (val entry : entries) {
        out.writeLong(LocationEmbedded.warp(entry.location()).position());
        out.writeUTF(entry.blockId().asString());
        out.writeInt(entry.content().length);
        out.write(entry.content());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
