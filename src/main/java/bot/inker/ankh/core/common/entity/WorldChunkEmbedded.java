package bot.inker.ankh.core.common.entity;

import bot.inker.ankh.core.api.entity.ChunkStorage;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import org.bukkit.Chunk;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Access(AccessType.FIELD)
public final class WorldChunkEmbedded implements ChunkStorage, Serializable {
  @Nonnull
  private UUID worldId;
  private long chunkId;

  @Nonnull
  public static WorldChunkEmbedded of(UUID worldId, int x, int z) {
    return of(worldId, chunkKeyFromLocation(x, z));
  }

  @Nonnull
  public static WorldChunkEmbedded of(@Nonnull Chunk chunk) {
    return of(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
  }

  @Nonnull
  public static WorldChunkEmbedded of(UUID worldId, long chunkId) {
    Objects.requireNonNull(worldId);
    WorldChunkEmbedded worldChunkEmbedded = new WorldChunkEmbedded();
    worldChunkEmbedded.worldId = worldId;
    worldChunkEmbedded.chunkId = chunkId;
    return worldChunkEmbedded;
  }

  @Nonnull
  public static WorldChunkEmbedded warp(@Nonnull ChunkStorage chunk) {
    if (chunk instanceof WorldChunkEmbedded) {
      return (WorldChunkEmbedded) chunk;
    }
    return WorldChunkEmbedded.of(
      chunk.worldId(),
      chunkKeyFromLocation(chunk.x(), chunk.z())
    );
  }

  private static long chunkKeyFromLocation(int x, int z) {
    return (((long) x) << 32) | (z & 0xFFFFFFFFL);
  }

  private static int xFromChunkKey(long chunkKey) {
    return (int) (chunkKey >> 32);
  }

  private static int zFromChunkKey(long chunkKey) {
    return (int) chunkKey;
  }

  @Nonnull
  @Override
  public UUID worldId() {
    return worldId;
  }

  @Nonnull
  @Override
  public WorldChunkEmbedded worldId(@Nonnull UUID worldId) {
    return of(worldId, chunkId);
  }

  public long chunkId() {
    return chunkId;
  }

  @Nonnull
  public WorldChunkEmbedded chunkId(long chunkId) {
    return of(worldId, chunkId);
  }

  @Override
  public int x() {
    return xFromChunkKey(chunkId);
  }

  @Nonnull
  @Override
  public WorldChunkEmbedded x(int x) {
    return of(worldId, chunkKeyFromLocation(x, z()));
  }

  @Override
  public int z() {
    return zFromChunkKey(chunkId);
  }

  @Nonnull
  @Override
  public WorldChunkEmbedded z(int z) {
    return of(worldId, chunkKeyFromLocation(x(), z));
  }

  @Override
  public String toString() {
    return "WorldChunkEmbedded{" +
      "worldId=" + worldId +
      ", x=" + x() +
      ", z=" + z() +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof ChunkStorage) {
      if (o instanceof WorldChunkEmbedded) {
        WorldChunkEmbedded that = (WorldChunkEmbedded) o;
        if (this.chunkId != that.chunkId) return false;
        return this.worldId.equals(that.worldId);
      } else {
        ChunkStorage that = (ChunkStorage) o;
        if (this.x() != that.x()) return false;
        if (this.z() != that.z()) return false;
        return this.worldId().equals(that.worldId());
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = worldId.hashCode();
    result = 31 * result + x();
    result = 31 * result + z();
    return result;
  }

  @Singleton
  public static class Factory implements ChunkStorage.Factory {
    @Override
    public ChunkStorage of(Chunk chunk) {
      return WorldChunkEmbedded.of(chunk);
    }

    @Override
    public ChunkStorage of(UUID worldId, int x, int z) {
      return WorldChunkEmbedded.of(worldId, x, z);
    }
  }
}
