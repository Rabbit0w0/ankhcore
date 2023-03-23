package bot.inker.ankh.core.api.entity;

import bot.inker.ankh.core.api.ioc.DcLazy;
import bot.inker.ankh.core.api.ioc.IocLazy;
import org.bukkit.Chunk;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * ChunkStorage used in storage, It's immutable instance
 * All modify method return a new instance with new value
 * <p>
 * In storage, it has 24 bytes, ChunkStorage(UUID_16 + Long_8)
 * ChunkStorage.position = aaaabbbb (seq as bytes)
 * a = x, b = z
 * <p>
 * It should have standard hashcode method and standard equals method
 * toString depends on implementation
 * <code>
 * public int hashCode() {
 * int result = worldId.hashCode();
 * result = 31 * result + x();
 * result = 31 * result + z();
 * return result;
 * }
 * </code>
 */
public interface ChunkStorage {
  static Factory factory() {
    return Factory.INSTANCE.get();
  }

  static ChunkStorage of(Chunk chunk) {
    return factory().of(chunk);
  }

  static ChunkStorage of(UUID worldId, int x, int z) {
    return factory().of(worldId, x, z);
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
  ChunkStorage worldId(@Nonnull UUID worldId);

  /**
   * @return x as chunk location
   */
  int x();

  /**
   * @param x new value
   * @return new instance with new value
   */
  @Nonnull
  ChunkStorage x(int x);

  /**
   * @return z as chunk location
   */
  int z();

  /**
   * @param z new value
   * @return new instance with new value
   */
  @Nonnull
  ChunkStorage z(int z);

  interface Factory {
    DcLazy<Factory> INSTANCE = IocLazy.of(Factory.class);

    ChunkStorage of(Chunk chunk);

    ChunkStorage of(UUID worldId, int x, int z);
  }
}
