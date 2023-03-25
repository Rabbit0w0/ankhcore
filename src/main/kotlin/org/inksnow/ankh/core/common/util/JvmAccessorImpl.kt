package org.inksnow.ankh.core.common.util

import bot.inker.acj.JvmHacker
import org.inksnow.ankh.core.api.util.JvmAccessor
import sun.misc.Unsafe
import java.lang.instrument.Instrumentation
import java.lang.invoke.MethodHandles
import javax.inject.Singleton

@Singleton
class JvmAccessorImpl : JvmAccessor {
  private val instrumentation: Instrumentation by lazy { JvmHacker.instrumentation() }
  private val unsafe: Unsafe by lazy { JvmHacker.unsafe() }
  private val lookup: MethodHandles.Lookup by lazy { JvmHacker.lookup() }

  override fun instrumentation() = instrumentation

  override fun unsafe() = unsafe

  override fun lookup() = lookup
}