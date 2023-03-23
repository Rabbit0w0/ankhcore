package bot.inker.ankh.core.common

import bot.inker.ankh.core.api.util.IRegistry
import bot.inker.ankh.core.common.dsl.ensurePrimaryThread
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import java.util.concurrent.atomic.AtomicReference

abstract class AbstractRegistry<T : Keyed> : IRegistry<T> {
  private var mapRef = AtomicReference(emptyMap<NamespacedKey, T>())
  override fun register(instance: T) {
    ensurePrimaryThread()

    val map = mapRef.get()
    val key = instance.key

    require(map[key] == null) { "id '${key}' have been registered." }
    val newMap = HashMap<NamespacedKey, T>(map.size + 1)
    newMap.putAll(newMap)
    newMap[key] = instance
    mapRef.set(newMap)
  }

  override fun require(key: NamespacedKey): T {
    return mapRef.get()[key]
      ?: throw IllegalStateException("id '$key' not found in registry")
  }

  override fun get(key: NamespacedKey?): T? {
    key ?: return null
    return mapRef.get()[key]
  }
}