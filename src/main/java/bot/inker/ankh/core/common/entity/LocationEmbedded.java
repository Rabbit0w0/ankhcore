package bot.inker.ankh.core.common.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.io.Serializable;

@Embeddable
@Access(AccessType.FIELD)
public class LocationEmbedded implements Serializable {
  @Nonnull
  private WorldChunkEmbedded chunk;
  private long position;

  @Nonnull
  public static LocationEmbedded of(WorldChunkEmbedded chunk, long position) {
    LocationEmbedded locationEmbedded = new LocationEmbedded();
    locationEmbedded.chunk = chunk;
    locationEmbedded.position = position;
    return locationEmbedded;
  }

  @Nonnull
  public static LocationEmbedded of(Location location) {
    LocationEmbedded locationEmbedded = new LocationEmbedded();
    WorldChunkEmbedded worldChunkEmbedded = WorldChunkEmbedded.of(location.getChunk());
    locationEmbedded.chunk = worldChunkEmbedded;
    short xOffset = (short) (location.getBlockX() - 16 * worldChunkEmbedded.x());
    int y = location.getBlockY();
    short zOffset = (short) (location.getBlockZ() - 16 * worldChunkEmbedded.z());
    locationEmbedded.position = positionFromLocation(xOffset, y, zOffset);
    return locationEmbedded;
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

  @Nonnull
  public WorldChunkEmbedded chunk() {
    return chunk;
  }

  @Nonnull
  public LocationEmbedded chunk(@Nonnull WorldChunkEmbedded chunk) {
    return of(chunk, position);
  }

  public long position() {
    return position;
  }

  public void position(long position) {
    this.position = position;
  }

  public int x() {
    return 16 * chunk.x() + xFromPosition(position);
  }

  @Nonnull
  public LocationEmbedded x(int x) {
    return of(chunk, positionFromLocation((short) (x - 16 * chunk.x()), y(), zFromPosition(position)));
  }

  public int y() {
    return yFromPosition(position);
  }

  @Nonnull
  public LocationEmbedded y(int y) {
    return of(chunk, positionFromLocation(xFromPosition(position), y, zFromPosition(position)));
  }

  public int z() {
    return 16 * chunk.z() + zFromPosition(position);
  }

  @Nonnull
  public LocationEmbedded z(int z) {
    return of(chunk, positionFromLocation(xFromPosition(position), y(), (short) (z - 16 * chunk.z())));
  }

  @Override
  public String toString() {
    return "LocationEmbedded{" +
      "world=" + chunk.worldId() +
      ", x=" + x() +
      ", y=" + y() +
      ", z=" + z() +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LocationEmbedded that = (LocationEmbedded) o;

    if (position != that.position) return false;
    return chunk.equals(that.chunk);
  }

  @Override
  public int hashCode() {
    int result = chunk.hashCode();
    result = 31 * result + (int) (position ^ (position >>> 32));
    return result;
  }
}
