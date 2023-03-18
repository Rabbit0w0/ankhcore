package bot.inker.ankh.core.common.util

object HexUtil {
  @JvmStatic
  private val DIGITS = byteArrayOf(
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
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
  )

  @JvmStatic
  private val LOWERCASE_DIGITS = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  )

  @JvmStatic
  @Throws(NumberFormatException::class)
  fun fromHexChecked(hex: CharSequence): ByteArray {
    val data = ByteArray(hex.length / 2)
    for (i in data.indices) {
      data[i] = (
        (DIGITS[hex[2 * i].code].toInt() shl 4).also {
          if (it < 0) {
            throw throw NumberFormatException("not a hexadecimal digit: \"" + hex[2 * i] + "\" = " + hex[2 * i].code)
          }
        } or
          (DIGITS[hex[2 * i + 1].code].toInt()).also {
            if (it < 0) {
              throw throw NumberFormatException("not a hexadecimal digit: \"" + hex[2 * i + 1] + "\" = " + hex[2 * i + 1].code)
            }
          }
        ).toByte()
    }
    return data
  }

  @JvmStatic
  fun fromHexChecked(hex: CharSequence, start: Int, end: Int): ByteArray {
    val data = ByteArray((end - start) / 2)
    for (i in data.indices) {
      data[i] = (
        (DIGITS[hex[start + 2 * i].code].toInt() shl 4).also {
          if (it < 0) {
            throw throw NumberFormatException("not a hexadecimal digit: \"" + hex[start + 2 * i] + "\" = " + hex[start + 2 * i].code)
          }
        } or
          (DIGITS[hex[start + 2 * i + 1].code].toInt()).also {
            if (it < 0) {
              throw throw NumberFormatException("not a hexadecimal digit: \"" + hex[start + 2 * i + 1] + "\" = " + hex[start + 2 * i + 1].code)
            }
          }
        ).toByte()
    }
    return data
  }

  @JvmStatic
  fun fromHex(hex: CharSequence): ByteArray {
    val data = ByteArray(hex.length / 2)
    for (i in data.indices) {
      data[i] = (DIGITS[hex[2 * i].code].toInt() shl 4 or DIGITS[hex[2 * i + 1].code].toInt()).toByte()
    }
    return data
  }

  @JvmStatic
  fun fromHex(hex: CharSequence, start: Int, end: Int): ByteArray {
    val data = ByteArray((end - start) / 2)
    for (i in data.indices) {
      data[i] =
        (DIGITS[hex[start + 2 * i].code].toInt() shl 4 or DIGITS[hex[start + 2 * i + 1].code].toInt()).toByte()
    }
    return data
  }

  @JvmStatic
  fun toHex(data: ByteArray): String {
    val hex = CharArray(data.size * 2)
    for (i in data.indices) {
      val b = data[i]
      hex[2 * i] = LOWERCASE_DIGITS[b.toInt() shr 4 and 0xf]
      hex[2 * i + 1] = LOWERCASE_DIGITS[b.toInt() and 0xf]
    }
    return String(hex)
  }

  @JvmStatic
  fun toHex(data: ByteArray, start: Int, end: Int): String {
    val hex = CharArray((end - start) * 2)
    for (i in start until end) {
      val b = data[i]
      hex[2 * i] = LOWERCASE_DIGITS[b.toInt() shr 4 and 0xf]
      hex[2 * i + 1] = LOWERCASE_DIGITS[b.toInt() and 0xf]
    }
    return String(hex)
  }
}
