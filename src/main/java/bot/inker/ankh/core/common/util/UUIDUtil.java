package bot.inker.ankh.core.common.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UUIDUtil {
  public static String unDash(final String id) {
    return id.substring(0, 8) +
      id.substring(9, 13) +
      id.substring(14, 18) +
      id.substring(19, 23) +
      id.substring(24, 36);
  }

  public static String withDash(final String id) {
    return id.substring(0, 8) + '-' +
      id.substring(8, 12) + '-' +
      id.substring(12, 16) + '-' +
      id.substring(16, 20) + '-' +
      id.substring(20, 32);
  }

  public static String toPlainString(final UUID id) {
    return toString0(id);
  }

  public static String toFullString(final UUID id) {
    return withDash(toString0(id));
  }

  private static String toString0(final UUID id) {
    return HexUtil.toHex(toBytes(id));
  }

  public static UUID fromString(final String s) {
    switch (s.length()) {
      case 32:
        return fromPlainString0(s);
      case 36:
        return fromPlainString0(unDash(s));
      default:
        throw new IllegalArgumentException("Invalid UUID: " + s);
    }
  }

  public static UUID fromFullString(final String s) {
    if (s.length() != 36) {
      throw new IllegalArgumentException("UUID not in full format: " + s);
    }
    return fromPlainString0(unDash(s));
  }

  public static UUID fromPlainString(final String s) {
    if (s.length() != 32) {
      throw new IllegalArgumentException("UUID not in plain format: " + s);
    }
    return fromPlainString0(s);
  }

  private static UUID fromPlainString0(final String s) {
    try {
      return fromBytes0(HexUtil.fromHex(s), 0);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID: " + s);
    }
  }

  public static byte[] toBytes(final UUID id) {
    byte[] result = new byte[16];
    long lsb = id.getLeastSignificantBits();
    for (int i = 15; i >= 8; --i) {
      result[i] = (byte) ((int) (lsb & 255L));
      lsb >>= 8;
    }
    long msb = id.getMostSignificantBits();
    for (int i = 7; i >= 0; --i) {
      result[i] = (byte) ((int) (msb & 255L));
      msb >>= 8;
    }
    return result;
  }

  public static UUID fromBytes(final byte[] bytes) {
    if (bytes.length != 16) {
      throw new IllegalArgumentException("Invalid length: " + bytes.length);
    }
    return fromBytes0(bytes, 0);
  }

  public static UUID fromBytes(final byte[] bytes, final int start) {
    if (bytes.length - start < 16) {
      throw new IllegalArgumentException("Invalid length: " + bytes.length);
    }
    return fromBytes0(bytes, start);
  }

  private static UUID fromBytes0(byte[] bytes, int start) {
    long msb = (bytes[start] & 255L) << 56
      | (bytes[start + 1] & 255L) << 48
      | (bytes[start + 2] & 255L) << 40
      | (bytes[start + 3] & 255L) << 32
      | (bytes[start + 4] & 255L) << 24
      | (bytes[start + 5] & 255L) << 16
      | (bytes[start + 6] & 255L) << 8
      | bytes[start + 7] & 255L;
    long lsb = (bytes[start + 8] & 255L) << 56
      | (bytes[start + 9] & 255L) << 48
      | (bytes[start + 10] & 255L) << 40
      | (bytes[start + 11] & 255L) << 32
      | (bytes[start + 12] & 255L) << 24
      | (bytes[start + 13] & 255L) << 16
      | (bytes[start + 14] & 255L) << 8
      | bytes[start + 15] & 255L;
    return new UUID(msb, lsb);
  }
}
