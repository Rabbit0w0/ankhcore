package org.inksnow.ankh.core.common.util

import org.objectweb.asm.ClassWriter

class ClassWriterWithClassLoader(
  private val classLoader: ClassLoader?,
  flags: Int,
) : ClassWriter(flags) {
  override fun getClassLoader(): ClassLoader? {
    return classLoader
  }
}