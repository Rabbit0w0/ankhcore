package org.inksnow.ankh.core.common;

import com.google.inject.name.Names;
import lombok.val;
import net.kyori.adventure.key.Key;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer;
import org.inksnow.ankh.core.common.util.CheckUtil;
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Singleton
public class AnkhServiceLoaderImpl implements AnkhServiceLoader {
  private static final AtomicReference<Map<String, AnkhPluginContainerImpl>> pluginRegistry = new AtomicReference<>(Collections.emptyMap());
  private static final AtomicReference<Map<KeyCacheKey, Object>> keyInstanceMap = new AtomicReference<>(new HashMap<>());
  private static final AtomicReference<Map<StringCacheKey, Object>> stringInstanceMap = new AtomicReference<>(new HashMap<>());
  private static final AtomicReference<Map<KeyCacheKey, Object>> keyCacheMap = new AtomicReference<>(new ConcurrentHashMap<>());
  private static final AtomicReference<Map<StringCacheKey, Object>> stringCacheMap = new AtomicReference<>(new ConcurrentHashMap<>());
  private static final Function<KeyCacheKey, Object> keyLoadFunction = it -> {
    val keyInstance = keyInstanceMap.get().get(it);
    if (keyInstance != null) {
      return keyInstance;
    }
    val pluginContainer = pluginRegistry.get().get(it.namespace);
    val injector = pluginContainer.getInjector();
    val iocKey = com.google.inject.Key.get(it.clazz, Names.named(it.value));
    val binding = injector.getExistingBinding(iocKey);
    return binding != null ? injector.getInstance(iocKey) : null;
  };

  private static final Function<StringCacheKey, Object> stringLoadFunction = it -> {
    val stringInstance = stringInstanceMap.get().get(it);
    if (stringInstance != null) {
      return stringInstance;
    }
    val named = Names.named(it.key);
    for (AnkhPluginContainerImpl ankhPluginContainer : pluginRegistry.get().values()) {
      val injector = ankhPluginContainer.getInjector();
      val iocKey = com.google.inject.Key.get(it.clazz, named);
      val binding = injector.getExistingBinding(iocKey);
      if (binding != null) {
        val instance = injector.getInstance(iocKey);
        keyCacheMap.get().put(new KeyCacheKey(Key.key(ankhPluginContainer.getPluginYml().getName(), it.key), it.clazz), instance);
        return instance;
      }
    }
    return null;
  };

  public static void staticRegisterPlugin(@Nonnull String name, @Nonnull AnkhPluginContainerImpl container) {
    CheckUtil.ensureMainThread();

    for (KeyCacheKey key : keyInstanceMap.get().keySet()) {
      if (name.equals(key.namespace)) {
        throw new IllegalStateException("namespace '" + key.namespace + "' is a plugin namespace, it should be registered by ioc");
      }
    }

    val rawMap = pluginRegistry.get();
    val newMap = new HashMap<String, AnkhPluginContainerImpl>(rawMap.size() + 1);
    newMap.putAll(rawMap);
    newMap.put(name, container);
    pluginRegistry.set(newMap);

    keyCacheMap.set(new ConcurrentHashMap<>());
    stringCacheMap.set(new ConcurrentHashMap<>());
  }

  public static <T> void staticRegisterService(@Nonnull Key key, @Nonnull Class<T> serviceClass, T instance) {
    CheckUtil.ensureMainThread();
    if (pluginRegistry.get().containsKey(key.namespace())) {
      throw new IllegalStateException("namespace '" + key.namespace() + "' is a plugin namespace, it should be registered by ioc");
    }
    { // register key instance map
      val cacheKey = new KeyCacheKey(key, serviceClass);
      val rawMap = keyInstanceMap.get();
      if (rawMap.containsKey(cacheKey)) {
        throw new IllegalStateException("Register service '" + serviceClass + "' key '" + key + "' multi times");
      }
      val newMap = new HashMap<KeyCacheKey, Object>(rawMap.size() + 1);
      newMap.putAll(rawMap);
      newMap.put(new KeyCacheKey(key, serviceClass), instance);
      keyInstanceMap.set(newMap);
    }
    { // register string instance map
      val cacheKey = new StringCacheKey(key.value(), serviceClass);
      val rawMap = stringInstanceMap.get();
      if (!rawMap.containsKey(cacheKey)) {
        val newMap = new HashMap<StringCacheKey, Object>(rawMap.size() + 1);
        newMap.putAll(rawMap);
        newMap.put(new StringCacheKey(key.value(), serviceClass), instance);
        stringInstanceMap.set(newMap);
      }
    }
    keyCacheMap.set(new ConcurrentHashMap<>());
    stringCacheMap.set(new ConcurrentHashMap<>());
  }

  @SuppressWarnings("unchecked")
  public static <T> @Nonnull T staticLoadService(@Nonnull String key, @Nonnull Class<T> clazz) {
    if (key.contains(":")) {
      return staticLoadService(Key.key(key), clazz);
    }
    val result = (T) stringCacheMap.get().computeIfAbsent(new StringCacheKey(key, clazz), stringLoadFunction);
    if (result == null) {
      throw new IllegalStateException("No named service " + key + " " + clazz + " found");
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T> @Nonnull T staticLoadService(@Nonnull Key key, @Nonnull Class<T> clazz) {
    val result = (T) keyCacheMap.get().computeIfAbsent(new KeyCacheKey(key, clazz), keyLoadFunction);
    if (result == null) {
      throw new IllegalStateException("No named service " + key + " " + clazz + " found");
    }
    return result;
  }

  @Override
  public <T> void registerServiceImpl(@Nonnull Key key, @Nonnull Class<T> serviceClass, T instance) {
    staticRegisterService(key, serviceClass, instance);
  }

  @Override
  public <T> T loadServiceImpl(@Nonnull String key, @Nonnull Class<T> clazz) {
    return staticLoadService(key, clazz);
  }

  @Override
  public <T> T loadServiceImpl(@Nonnull Key key, @Nonnull Class<T> clazz) {
    return staticLoadService(key, clazz);
  }

  private static class StringCacheKey {
    private final @Nonnull String key;
    private final @Nonnull Class<?> clazz;
    private final int hashCode;

    private StringCacheKey(@Nonnull String key, @Nonnull Class<?> clazz) {
      this.key = key;
      this.clazz = clazz;
      this.hashCode = 31 * key.hashCode() + clazz.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StringCacheKey that = (StringCacheKey) o;

      if (!key.equals(that.key)) return false;
      return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }
  }

  private static class KeyCacheKey {
    private final @Nonnull String namespace;
    private final @Nonnull String value;
    private final @Nonnull Class<?> clazz;
    private final int hashCode;

    private KeyCacheKey(@Nonnull Key key, @Nonnull Class<?> clazz) {
      this.namespace = key.namespace();
      this.value = key.value();
      this.clazz = clazz;
      this.hashCode = 31 * (31 * this.namespace.hashCode() + this.value.hashCode()) + clazz.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      KeyCacheKey keyCacheKey = (KeyCacheKey) o;

      if (!this.namespace.equals(keyCacheKey.namespace)) return false;
      if (!this.value.equals(keyCacheKey.value)) return false;
      return clazz.equals(keyCacheKey.clazz);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }
  }
}
