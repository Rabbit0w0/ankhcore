@file:Suppress("NOTHING_TO_INLINE")

package bot.inker.ankh.core.common.dsl

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KProperty

inline operator fun AtomicBoolean.setValue(
  obj: Any,
  property: KProperty<*>,
  value: Boolean,
) {
  set(value)
}

inline operator fun AtomicBoolean.getValue(obj: Any, property: KProperty<*>): Boolean {
  return get()
}
