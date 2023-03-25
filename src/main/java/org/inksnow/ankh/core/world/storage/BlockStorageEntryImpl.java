package org.inksnow.ankh.core.world.storage;

import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.key.Key;
import org.inksnow.ankh.core.api.storage.LocationStorage;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Data
@Builder
public class BlockStorageEntryImpl implements BlockStorageEntry {
  private final @Nonnull LocationStorage location;
  private final @Nonnull Key blockId;
  private final @Nonnull byte[] content;

  public static class Builder implements BlockStorageEntry.Builder {
    @Override
    public @Nonnull Builder getThis() {
      return this;
    }
  }

  @Singleton
  public static class Factory implements BlockStorageEntry.Factory {
    @Override
    public @Nonnull BlockStorageEntry.Builder builder() {
      return new Builder();
    }

    @Override
    public @Nonnull BlockStorageEntry of(@Nonnull LocationStorage location, @Nonnull Key blockId, @Nonnull byte[] content) {
      return new BlockStorageEntryImpl(location, blockId, content);
    }
  }
}
