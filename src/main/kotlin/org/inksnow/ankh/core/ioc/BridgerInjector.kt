package org.inksnow.ankh.core.ioc

import com.google.inject.Injector
import org.inksnow.ankh.core.api.ioc.AnkhInjector
import org.inksnow.ankh.core.api.ioc.AnkhIocKey
import javax.inject.Provider

class BridgerInjector(
  private val injector: Injector,
) : AnkhInjector {
  override fun injectMembers(instance: Any) {
    injector.injectMembers(instance)
  }

  override fun <T> getProvider(key: AnkhIocKey<T>): Provider<T> {
    return injector.getProvider((key as BridgerKey<T>).key)
  }

  override fun <T> getProvider(type: Class<T>): Provider<T> {
    return injector.getProvider(type)
  }

  override fun <T> getInstance(key: AnkhIocKey<T>): T {
    return injector.getInstance((key as BridgerKey<T>).key)
  }

  override fun <T> getInstance(type: Class<T>): T {
    return injector.getInstance(type)
  }

  override fun getParent(): AnkhInjector {
    return BridgerInjector(injector.parent)
  }
}