package org.inksnow.ankh.core.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.inksnow.ankh.core.api.util.IRegistry;
import org.inksnow.ankh.core.common.util.CheckUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AbstractRegistry<T extends Keyed> implements IRegistry<T> {
  private final AtomicReference<Map<StoreKey, T>> mapRef = new AtomicReference<>(Collections.emptyMap());

  @Override
  public void register(@Nonnull T instance) {
    CheckUtil.ensureMainThread();

    val map = mapRef.get();
    val key = StoreKey.warp(instance.key());

    if (map.containsKey(key)) {
      throw new IllegalArgumentException("id '" + key + "' have been registered.");
    }

    val newMap = new HashMap<StoreKey, T>(map.size() + 1);
    newMap.putAll(map);
    newMap.put(key, instance);

    mapRef.set(newMap);
  }

  @Override
  public @Nonnull T require(@Nonnull Key key) {
    T instance = mapRef.get().get(StoreKey.warp(key));
    if (instance == null) {
      throw new IllegalArgumentException("id '" + key + "' not found in registry");
    }
    return instance;
  }

  @Override
  public @Nullable T get(@Nonnull Key key) {
    return mapRef.get().get(StoreKey.warp(key));
  }

  @AllArgsConstructor
  @EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
  private static class StoreKey {
    private final String namespace;
    private final String value;

    public static StoreKey warp(Key key) {
      return new StoreKey(key.namespace(), key.value());
    }

    @Override
    public String toString() {
      return namespace + ":" + value;
    }
  }
}
