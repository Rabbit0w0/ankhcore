package org.inksnow.ankh.core.plugin

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.util.Modules
import org.bukkit.Bukkit
import org.bukkit.event.EventPriority
import org.bukkit.plugin.PluginDescriptionFile
import org.inksnow.ankh.core.api.AnkhCore
import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer
import org.inksnow.ankh.core.api.plugin.AnkhPluginYml
import org.inksnow.ankh.core.common.AnkhServiceLoaderImpl
import org.inksnow.ankh.core.common.dsl.logger
import org.inksnow.ankh.core.ioc.BridgerInjector
import org.inksnow.ankh.loader.AnkhClassLoader
import org.inksnow.ankh.loader.AnkhCoreLoaderPlugin
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class AnkhPluginContainerImpl(
  val pluginClass: Class<out AnkhBukkitPlugin>,
  val classLoader: AnkhClassLoader,
  val descriptionFile: PluginDescriptionFile,
  val pluginYml: AnkhPluginYml,
) : AnkhPluginContainer {
  private val logger by logger(descriptionFile.name)
  lateinit var injector: Injector private set
  lateinit var bukkitPlugin: AnkhBukkitPlugin private set
  val pluginModules = ArrayList<Class<out Module>>()

  private val clinitListeners = ListenerSet()
  private val initListeners = ListenerSet()
  private val loadListeners = ListenerSet()
  private val enableListeners = ListenerSet()
  private val disableListeners = ListenerSet()

  override fun onClinit(priority: EventPriority, listener: Runnable) {
    clinitListeners.register(priority, listener)
  }

  override fun onInit(priority: EventPriority, listener: Runnable) {
    initListeners.register(priority, listener)
  }

  override fun onLoad(priority: EventPriority, listener: Runnable) {
    loadListeners.register(priority, listener)
  }

  override fun onEnable(priority: EventPriority, listener: Runnable) {
    enableListeners.register(priority, listener)
  }

  override fun onDisable(priority: EventPriority, listener: Runnable) {
    disableListeners.register(priority, listener)
  }

  override fun plugin() = bukkitPlugin

  override fun callClinit() = ensureSuccess {
    clinitListeners.call()
  }

  @Suppress("UNCHECKED_CAST")
  override fun callInit(bukkitPlugin: AnkhBukkitPlugin) =
    ensureSuccess {
      this.bukkitPlugin = bukkitPlugin
      if (pluginClass == AnkhCoreLoaderPlugin::class.java) {
        this.injector = Guice.createInjector(
          pluginModules.map { it.getConstructor().newInstance() }.let(Modules::combine),
          { binder ->
            binder.bind(AnkhCoreLoaderPlugin::class.java).toInstance(bukkitPlugin as AnkhCoreLoaderPlugin)
          },
        )
        AnkhCore.`$internal$actions$`.setInjector(BridgerInjector(this.injector))
      } else {
        this.injector = (AnkhCoreLoaderPlugin.container as AnkhPluginContainerImpl).injector.createChildInjector(
          pluginModules.map { it.getConstructor().newInstance() }.let(Modules::combine),
          { binder ->
            binder.bind(AnkhPluginContainer::class.java)
              .to(AnkhPluginContainerImpl::class.java)
            binder.bind(AnkhPluginContainerImpl::class.java).toInstance(this)

            binder.bind(AnkhBukkitPlugin::class.java)
              .to(bukkitPlugin::class.java)
            binder.bind(bukkitPlugin::class.java as Class<AnkhBukkitPlugin>)
              .toInstance(bukkitPlugin)
          },
        )
      }
      AnkhServiceLoaderImpl.staticRegisterPlugin(pluginYml.name, this)
      initListeners.call()
    }

  override fun callLoad() = ensureSuccess {
    loadListeners.call()
  }

  override fun callEnable() = ensureSuccess {
    enableListeners.call()
  }

  override fun callDisable() = ensureSuccess {
    disableListeners.call()
  }

  private class ListenerSet {
    private val loadIdAllocator = AtomicInteger(Int.MIN_VALUE)
    private var loadLock = false
    private val listeners = TreeSet<SortEntry>()

    @Synchronized
    fun register(priority: EventPriority, listener: Runnable) {
      check(!loadLock) { "ListenerSet have been called." }
      listeners.add(SortEntry(listener, priority, loadIdAllocator.getAndIncrement()))
    }

    @Synchronized
    fun call() {
      check(!loadLock) { "ListenerSet have been called." }
      loadLock = true
      listeners.forEach { it.listener.run() }
    }

    private class SortEntry(
      val listener: Runnable,
      val priority: EventPriority,
      val loadId: Int,
    ) : Comparable<SortEntry> {
      override fun compareTo(other: SortEntry): Int {
        val orderCompareResult = priority.compareTo(other.priority)
        if (orderCompareResult != 0) {
          return orderCompareResult
        }
        return loadId.compareTo(other.loadId)
      }

      override fun equals(other: Any?): Boolean {
        if (this === other) {
          return true
        }
        if (other == null || other !is SortEntry) {
          return false
        }
        return this.loadId == other.loadId
      }

      override fun hashCode(): Int {
        return this.loadId
      }
    }
  }

  private inline fun ensureSuccess(action: () -> Unit) {
    try {
      action()
    } catch (e: Throwable) {
      Bukkit.getServer().shutdown()
      throw e
    }
  }
}