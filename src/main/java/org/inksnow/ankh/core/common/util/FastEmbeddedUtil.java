package org.inksnow.ankh.core.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FastEmbeddedUtil {
  public static long chunk_chunkId(int x, int z) {
    return (((long) x) << 32) | (z & 0xFFFFFFFFL);
  }

  public static int chunkX(long chunkId) {
    return (int) (chunkId >> 32);
  }

  public static int chunkZ(long chunkId) {
    return (int) chunkId;
  }

  public static long location_chunkId(int x, int z) {
    return (((long) (x >> 4)) << 32) | ((z >> 4) & 0xFFFFFFFFL);
  }

  public static long blockId(int x, int y, int z) {
    return (((long) (x & 0xf)) << 48) | (((long) y) << 16) | (z & 0xf);
  }

  public static int chunk_blockId_x(long chunkId, long blockId) {
    return (int) ((chunkId & 0xFFFFFFFF00000000L) >> 28 | (blockId >> 48));
  }

  public static int blockId_y(long blockId) {
    return (int) (blockId >> 16);
  }

  public static int chunk_blockId_z(long chunkId, long blockId) {
    return (int) ((chunkId & 0xFFFFFFFFL) << 4 | (blockId & 0xFFFF));
  }

  public static int chunkX_blockId_x(int chunkX, long blockId) {
    return (int) (chunkX << 4 | (blockId >> 48));
  }

  public static int chunkZ_blockId_z(int chunkZ, long blockId) {
    return (int) (chunkZ << 4 | (blockId & 0xFFFF));
  }
}
