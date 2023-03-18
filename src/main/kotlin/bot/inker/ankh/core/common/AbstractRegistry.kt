package bot.inker.ankh.core.common

import bot.inker.ankh.core.api.util.IRegistry
import org.bukkit.Keyed
import org.bukkit.NamespacedKey

abstract class AbstractRegistry<T : Keyed>:IRegistry<T> {
  private val map = HashMap<NamespacedKey, T>()
  override fun register(instance: T) {
    require(map.putIfAbsent(instance.key, instance) == null) { "id '${instance.key}' have been registered." }
  }

  override fun require(key: NamespacedKey): T {
    return map[key]
      ?: throw IllegalStateException("id '$key' not found in registry")
  }

  override fun get(key: NamespacedKey?): T? {
    key ?: return null
    return map[key]
  }
}