package org.inksnow.ankh.core.ioc

import com.google.inject.Key
import org.inksnow.ankh.core.api.ioc.AnkhIocKey
import java.lang.reflect.Type
import javax.inject.Singleton

class BridgerKey<T>(
  val key: Key<T>,
) : AnkhIocKey<T> {
  @Singleton
  class Factory : AnkhIocKey.Factory {
    override fun <T> get(type: Class<T>): AnkhIocKey<T> {
      return BridgerKey(Key.get(type))
    }

    override fun <T> get(
      type: Class<T>,
      annotationType: Class<out Annotation>,
    ): AnkhIocKey<T> {
      return BridgerKey(Key.get(type, annotationType))
    }

    override fun <T> get(
      type: Class<T>,
      annotation: Annotation,
    ): AnkhIocKey<T> {
      return BridgerKey(Key.get(type, annotation))
    }

    override fun get(type: Type): AnkhIocKey<*> {
      return BridgerKey(Key.get(type))
    }

    override fun get(
      type: Type,
      annotationType: Class<out Annotation>,
    ): AnkhIocKey<*> {
      return BridgerKey(Key.get(type, annotationType))
    }

    override fun get(
      type: Type,
      annotation: Annotation,
    ): AnkhIocKey<*> {
      return BridgerKey(Key.get(type, annotation))
    }
  }
}