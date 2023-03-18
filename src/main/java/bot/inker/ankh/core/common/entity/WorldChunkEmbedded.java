package bot.inker.ankh.core.common.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import org.bukkit.Chunk;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Access(AccessType.FIELD)
public class WorldChunkEmbedded implements Serializable {
  @Nonnull
  private UUID worldId;
  private long chunkId;

  @Nonnull
  public static WorldChunkEmbedded of(UUID worldId, long chunkId) {
    Objects.requireNonNull(worldId);
    WorldChunkEmbedded worldChunkEmbedded = new WorldChunkEmbedded();
    worldChunkEmbedded.worldId = worldId;
    worldChunkEmbedded.chunkId = chunkId;
    return worldChunkEmbedded;
  }

  @Nonnull
  public static WorldChunkEmbedded of(Chunk chunk) {
    WorldChunkEmbedded worldChunkEmbedded = new WorldChunkEmbedded();
    worldChunkEmbedded.worldId = chunk.getWorld().getUID();
    worldChunkEmbedded.chunkId = chunkKeyFromLocation(chunk.getX(), chunk.getZ());
    return worldChunkEmbedded;
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
  public UUID worldId() {
    return worldId;
  }

  @Nonnull
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

  public int x() {
    return xFromChunkKey(chunkId);
  }

  @Nonnull
  public WorldChunkEmbedded x(int x) {
    return of(worldId, chunkKeyFromLocation(x, z()));
  }

  public int z() {
    return zFromChunkKey(chunkId);
  }

  @Nonnull
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
    if (o == null || getClass() != o.getClass()) return false;

    WorldChunkEmbedded that = (WorldChunkEmbedded) o;

    if (chunkId != that.chunkId) return false;
    return worldId.equals(that.worldId);
  }

  @Override
  public int hashCode() {
    int result = worldId.hashCode();
    result = 31 * result + (int) (chunkId ^ (chunkId >>> 32));
    return result;
  }
}
