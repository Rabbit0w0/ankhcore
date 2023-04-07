package org.inksnow.ankh.core.api.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * All registry interface
 *
 * @param <T> type this registry managed
 */
public interface IRegistry<T extends Keyed> {
  /**
   * register the instance
   *
   * @param instance the instance to register
   * @throws IllegalStateException if key have been used
   */
  void register(@Nonnull T instance);

  /**
   * require instance by key
   *
   * @param key key
   * @return instance
   * @throws IllegalStateException if key not exist
   */
  @Nonnull
  T require(@Nonnull Key key);

  /**
   * get instance by key
   *
   * @param key key
   * @return instance
   */
  @Nullable
  T get(@Nonnull Key key);
}
