package org.inksnow.ankh.core.world.storage;

import lombok.val;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.inksnow.ankh.core.api.AnkhCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.*;

@Singleton
public class PdcWorldStorage extends AbstractIoWorldStorage {
  private static final NamespacedKey CHUNK_STORAGE_KEY = new NamespacedKey(AnkhCore.PLUGIN_ID, "chunk-storage");

  @Override
  public @Nullable InputStream openInputStream(@Nonnull Chunk chunk) {
    val fullBytes = chunk.getPersistentDataContainer().get(CHUNK_STORAGE_KEY, PersistentDataType.BYTE_ARRAY);
    if (fullBytes == null) {
      return null;
    }
    return new ByteArrayInputStream(fullBytes);
  }

  @Override
  public @Nonnull OutputStream openOutputStream(@Nonnull Chunk chunk) {
    return new CloseStoreOutputStream(chunk);
  }

  private static class CloseStoreOutputStream extends ByteArrayOutputStream {
    private final Chunk chunk;
    private volatile boolean closed = false;

    private CloseStoreOutputStream(Chunk chunk) {
      this.chunk = chunk;
    }

    private <T extends Throwable> void ensureOpen() throws T {
      if (closed) {
        throw (T) new IOException("Stream closed");
      }
    }

    @Override
    public synchronized void write(int b) {
      ensureOpen();
      super.write(b);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
      ensureOpen();
      super.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
      if (!this.closed) {
        this.closed = true;
        chunk.getPersistentDataContainer().set(CHUNK_STORAGE_KEY, PersistentDataType.BYTE_ARRAY, this.toByteArray());
      }
    }
  }
}
