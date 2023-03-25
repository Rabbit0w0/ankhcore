package bot.inker.ankh.core.common.entity;

import bot.inker.ankh.core.api.storage.ChunkStorage;
import bot.inker.ankh.core.api.storage.LocationStorage;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Access(AccessType.FIELD)
public final class LocationEmbedded implements LocationStorage, Serializable {
  @Nonnull
  private WorldChunkEmbedded chunk;
  private long position;

  public static @Nonnull LocationEmbedded of(ChunkStorage chunkStorage, long position) {
    LocationEmbedded locationEmbedded = new LocationEmbedded();
    locationEmbedded.chunk = WorldChunkEmbedded.warp(chunkStorage);
    locationEmbedded.position = position;
    return locationEmbedded;
  }

  public static @Nonnull LocationEmbedded of(UUID worldId, int x, int y, int z) {
    return of(
      WorldChunkEmbedded.of(worldId, x >> 4, z >> 4),
      positionFromLocation((short) (x & 0xf), y, (short) (z & 0xf))
    );
  }

  public static @Nonnull LocationEmbedded of(Location location) {
    return of(
      location.getWorld().getUID(),
      location.getBlockX(),
      location.getBlockY(),
      location.getBlockZ()
    );
  }

  public static @Nonnull LocationEmbedded warp(LocationStorage location) {
    if (location instanceof LocationEmbedded) {
      return (LocationEmbedded) location;
    }
    return of(location.worldId(), location.x(), location.y(), location.z());
  }

  private static long positionFromLocation(short x, int y, short z) {
    if (x < 0 || x > 15) {
      throw new IllegalArgumentException("x should in [0, 15), but got " + x);
    }
    if (z < 0 || z > 15) {
      throw new IllegalArgumentException("z should in [0, 15), but got " + z);
    }
    return (((long) x) << 48) | (((long) y) << 16) | (z & 0xFFFFL);
  }

  private static short xFromPosition(long position) {
    return (short) (position >> 48);
  }

  private static int yFromPosition(long position) {
    return (int) (position >> 16);
  }

  private static short zFromPosition(long position) {
    return (short) position;
  }

  public @Nonnull UUID worldId() {
    return chunk.worldId();
  }

  @Nonnull
  @Override
  public LocationStorage worldId(@Nonnull UUID worldId) {
    return of(worldId, x(), y(), z());
  }

  @Nonnull
  @Override
  public WorldChunkEmbedded chunk() {
    return chunk;
  }

  public long position() {
    return position;
  }

  @Override
  public int x() {
    return chunk.x() << 4 | xFromPosition(position);
  }

  @Override
  public @Nonnull LocationStorage x(int x) {
    return of(worldId(), x, y(), z());
  }

  @Override
  public int y() {
    return yFromPosition(position);
  }

  @Override
  public @Nonnull LocationStorage y(int y) {
    return of(worldId(), x(), y, z());
  }

  @Override
  public int z() {
    return chunk.z() << 4 | zFromPosition(position);
  }

  @Override
  public @Nonnull LocationStorage z(int z) {
    return of(worldId(), x(), y(), z);
  }

  @Override
  public String toString() {
    return "LocationStorage{" +
      "world=" + chunk.worldId() +
      ", x=" + x() +
      ", y=" + y() +
      ", z=" + z() +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof LocationStorage) {
      if (o instanceof LocationEmbedded) {
        LocationEmbedded that = (LocationEmbedded) o;
        if (this.position != that.position) return false;
        return this.chunk.equals(that.chunk);
      } else {
        LocationStorage that = (LocationStorage) o;
        if (this.x() != that.x()) return false;
        if (this.y() != that.y()) return false;
        if (this.z() != that.z()) return false;
        return this.chunk().equals(that.chunk());
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = worldId().hashCode();
    result = 31 * result + x();
    result = 31 * result + y();
    result = 31 * result + z();
    return result;
  }

  @Singleton
  public static class Factory implements LocationStorage.Factory {
    @Override
    public @Nonnull LocationStorage of(@Nonnull UUID worldId, int x, int y, int z) {
      return LocationEmbedded.of(worldId, x, y, z);
    }

    @Override
    public @Nonnull LocationStorage of(@Nonnull Location location) {
      return LocationEmbedded.of(location);
    }
  }
}
