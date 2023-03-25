package org.inksnow.ankh.core.api.util;

import sun.misc.Unsafe;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;

public interface JvmAccessor {
  Instrumentation instrumentation();

  Unsafe unsafe();

  MethodHandles.Lookup lookup();
}
