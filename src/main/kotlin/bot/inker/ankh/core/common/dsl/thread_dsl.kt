@file:Suppress("NOTHING_TO_INLINE")
package bot.inker.ankh.core.common.dsl

import org.bukkit.Bukkit

inline fun ensurePrimaryThread() {
  check(Bukkit.isPrimaryThread()) {
    "require to run in primary thread"
  }
}

inline fun ensureAsyncThread() {
  check(!Bukkit.isPrimaryThread()) {
    "require to run in Async thread"
  }
}