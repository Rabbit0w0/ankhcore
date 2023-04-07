package org.inksnow.ankh.core.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HexUtil {
  private static final byte[] DIGITS = {
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1,
      -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
  };

  private static final char[] LOWERCASE_DIGITS = {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
  };

  public static byte[] fromHex(CharSequence hex) {
    byte[] data = new byte[hex.length() / 2];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) (DIGITS[hex.charAt(2 * i)] << 4 | DIGITS[hex.charAt(2 * i + 1)]);
    }
    return data;
  }

  public static byte[] fromHex(CharSequence hex, int start, int end) {
    byte[] data = new byte[(end - start) / 2];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) (DIGITS[hex.charAt(start + 2 * i)] << 4 | DIGITS[hex.charAt(start + 2 * i + 1)]);
    }
    return data;
  }

  public static String toHex(byte[] data) {
    char[] hex = new char[data.length * 2];
    for (int i = 0; i < data.length; i++) {
      byte b = data[i];
      hex[2 * i] = LOWERCASE_DIGITS[(b >> 4) & 0xf];
      hex[2 * i + 1] = LOWERCASE_DIGITS[b & 0xf];
    }
    return new String(hex);
  }

  public static String toHex(byte[] data, int start, int end) {
    char[] hex = new char[(end - start) * 2];
    for (int i = start; i < end; i++) {
      byte b = data[i];
      hex[2 * i] = LOWERCASE_DIGITS[(b >> 4) & 0xf];
      hex[2 * i + 1] = LOWERCASE_DIGITS[b & 0xf];
    }
    return new String(hex);
  }
}
