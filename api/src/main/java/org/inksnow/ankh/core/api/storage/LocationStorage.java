package org.inksnow.ankh.core.api.storage;

import org.bukkit.Location;
import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.ioc.IocLazy;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * LocationStorage used in storage, It's immutable instance
 * All modify method return a new instance with new value
 * <p>
 * In storage, it has 32 bytes, ChunkStorage(UUID_16 + Long_8)+LocationStorage(Long_8)
 * LocationStorage.position = aabbbbcc (seq as bytes)
 * a = x, b = y, c = z
 * <p>
 * It should have standard hashcode method and standard equals method
 * toString depends on implementation
 * <code>
 * public int hashCode() {
 * int result = worldId().hashCode();
 * result = 31 * result + x();
 * result = 31 * result + y();
 * result = 31 * result + z();
 * return result;
 * }
 * </code>
 *
 * @see ChunkStorage
 */
public interface LocationStorage {
  /**
   * get factory by ioc
   *
   * @return factory instance
   */
  static Factory factory() {
    return Factory.INSTANCE.get();
  }

  /**
   * same as <code>LocationStorage.factory().of(worldId, x, y, z);</code>
   *
   * @param worldId worldId, get from org.bukkit.World#getUID
   * @param x       block x location
   * @param y       block y location
   * @param z       block z location
   * @return location storage instance
   */
  static @Nonnull LocationStorage of(@Nonnull UUID worldId, int x, int y, int z) {
    return factory().of(worldId, x, y, z);
  }

  /**
   * same as <code>LocationStorage.factory().of(location);</code>
   *
   * @param location bukkit location instance
   * @return location storage instance
   */
  static @Nonnull LocationStorage of(@Nonnull Location location) {
    return factory().of(location);
  }

  /**
   * @return worldId
   */
  @Nonnull
  UUID worldId();

  /**
   * @param worldId new value
   * @return new instance with new value
   */
  @Nonnull
  LocationStorage worldId(@Nonnull UUID worldId);

  /**
   * @return chunk
   */
  @Nonnull
  ChunkStorage chunk();

  /**
   * @return x as block location
   */
  int x();

  /**
   * @param x new value
   * @return new instance with new value
   */
  @Nonnull
  LocationStorage x(int x);

  /**
   * @return y as block location
   */
  int y();

  /**
   * @param y new value
   * @return new instance with new value
   */
  @Nonnull
  LocationStorage y(int y);

  /**
   * @return z as block location
   */
  int z();

  /**
   * @param z new value
   * @return new instance with new value
   */
  @Nonnull
  LocationStorage z(int z);

  interface Factory {
    @ApiStatus.Internal
    DcLazy<Factory> INSTANCE = IocLazy.of(Factory.class);

    /**
     * Create instance by value
     *
     * @param worldId worldId, get from org.bukkit.World#getUID
     * @param x       block x location
     * @param y       block y location
     * @param z       block z location
     * @return location storage instance
     */
    @Nonnull
    LocationStorage of(@Nonnull UUID worldId, int x, int y, int z);

    /**
     * create location storage from Bukkit Location
     *
     * @param location bukkit location instance
     * @return location storage instance
     */
    @Nonnull
    LocationStorage of(@Nonnull Location location);
  }
}
