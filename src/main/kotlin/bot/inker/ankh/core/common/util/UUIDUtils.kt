@file:Suppress("NOTHING_TO_INLINE")

package bot.inker.ankh.core.common.util

import java.util.*

object UUIDUtils {
  @JvmStatic
  fun undash(id: String): String {
    return id.substring(0, 8) + id.substring(9, 13) + id.substring(14, 18) +
      id.substring(19, 23) + id.substring(24, 36)
  }

  @JvmStatic
  fun withDash(id: String): String {
    return id.substring(0, 8) + '-' + id.substring(8, 12) + '-' + id.substring(12, 16) + '-' +
      id.substring(16, 20) + '-' + id.substring(20, 32)
  }

  @JvmStatic
  fun toPlainString(id: UUID): String {
    return toString0(id)
  }

  @JvmStatic
  fun toFullString(id: UUID): String {
    return withDash(toString0(id))
  }

  @JvmStatic
  private inline fun toString0(id: UUID): String {
    return HexUtil.toHex(toBytes(id))
  }

  @JvmStatic
  fun fromString(s: String): UUID {
    return fromPlainString0(
      when (s.length) {
        36 -> undash(s)
        32 -> s
        else -> throw IllegalArgumentException("Invalid UUID: $s")
      }
    )
  }

  @JvmStatic
  fun fromFullString(s: String): UUID {
    require(s.length == 36) { "UUID not in full format: $s" }
    return fromPlainString0(undash(s))
  }

  @JvmStatic
  fun fromPlainString(s: String): UUID {
    require(s.length == 32) { "UUID not in plain format: $s" }
    return fromPlainString0(s)
  }

  @JvmStatic
  private inline fun fromPlainString0(s: String): UUID {
    assert(s.length == 32) { "invalid length: $s" }
    return try {
      fromBytes(HexUtil.fromHex(s))
    } catch (e: IllegalArgumentException) {
      throw IllegalArgumentException("Invalid UUID: $s")
    }
  }

  @JvmStatic
  fun toBytes(id: UUID): ByteArray {
    val result = ByteArray(16)
    var lsb = id.leastSignificantBits
    for (i in 15 downTo 8) {
      result[i] = (lsb and 0xffL).toByte()
      lsb = lsb shr 8
    }
    var msb = id.mostSignificantBits
    for (i in 7 downTo 0) {
      result[i] = (msb and 0xffL).toByte()
      msb = msb shr 8
    }
    return result
  }

  @JvmStatic
  fun fromBytes(bytes: ByteArray): UUID {
    require(bytes.size == 16) { "Invalid length: " + bytes.size }
    return fromBytes0(bytes, 0)
  }

  @JvmStatic
  fun fromBytes(bytes: ByteArray, start: Int): UUID {
    require(bytes.size - start >= 16) { "Invalid length: ${bytes.size - start}" }
    return fromBytes0(bytes, start)
  }

  @JvmStatic
  private inline fun fromBytes0(bytes: ByteArray, start: Int): UUID {
    assert(bytes.size - start >= 16) { "Invalid length: ${bytes.size - start}" }
    val msb = (bytes[start].toLong() and 0xFFL shl 56) or
      (bytes[start + 1].toLong() and 0xFFL shl 48) or
      (bytes[start + 2].toLong() and 0xFFL shl 40) or
      (bytes[start + 3].toLong() and 0xFFL shl 32) or
      (bytes[start + 4].toLong() and 0xFFL shl 24) or
      (bytes[start + 5].toLong() and 0xFFL shl 16) or
      (bytes[start + 6].toLong() and 0xFFL shl 8) or
      (bytes[start + 7].toLong() and 0xFFL)
    val lsb = (bytes[start + 8].toLong() and 0xFFL shl 56) or
      (bytes[start + 9].toLong() and 0xFFL shl 48) or
      (bytes[start + 10].toLong() and 0xFFL shl 40) or
      (bytes[start + 11].toLong() and 0xFFL shl 32) or
      (bytes[start + 12].toLong() and 0xFFL shl 24) or
      (bytes[start + 13].toLong() and 0xFFL shl 16) or
      (bytes[start + 14].toLong() and 0xFFL shl 8) or
      (bytes[start + 15].toLong() and 0xFFL)
    return UUID(msb, lsb)
  }
}
