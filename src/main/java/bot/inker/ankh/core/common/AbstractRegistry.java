package bot.inker.ankh.core.common;

import bot.inker.ankh.core.api.util.IRegistry;
import bot.inker.ankh.core.common.util.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AbstractRegistry<T extends Keyed> implements IRegistry<T> {
  private final AtomicReference<Map<Key, T>> mapRef = new AtomicReference<>(Collections.emptyMap());

  @Override
  public void register(@Nonnull T instance) {
    CheckUtil.ensureMainThread();

    val map = mapRef.get();
    val key = instance.key();

    if(map.containsKey(key)){
      throw new IllegalArgumentException("id '"+ key +"' have been registered.");
    }

    val newMap = new HashMap<Key, T>(map.size() + 1);
    newMap.putAll(map);
    newMap.put(key, instance);

    mapRef.set(newMap);
  }

  @Override
  public @Nonnull T require(@Nonnull Key key) {
    T instance = mapRef.get().get(key);
    if(instance == null){
      throw new IllegalArgumentException("id '"+key+"' not found in registry");
    }
    return instance;
  }

  @Override
  public @Nullable T get(@Nonnull Key key) {
    return mapRef.get().get(key);
  }
}
