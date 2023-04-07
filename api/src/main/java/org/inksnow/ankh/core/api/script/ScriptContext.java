package org.inksnow.ankh.core.api.script;

import org.bukkit.entity.Player;
import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.ioc.IocLazy;
import org.inksnow.ankh.core.api.util.IBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * ankh script engine context
 */
public interface ScriptContext {
  /**
   * get factory instance
   *
   * @return factory instance
   */
  static @Nonnull Factory factory() {
    return $internal$actions$.FACTORY.get();
  }

  /**
   * create builder instance
   *
   * @return builder instance
   */
  static @Nonnull Builder builder() {
    return factory().builder();
  }

  /**
   * get player
   *
   * @return player
   */
  @Nullable
  Player player();

  /**
   * require player
   *
   * @return player
   * @throws IllegalStateException if no player in context
   */
  @Nonnull
  Player requirePlayer();

  /**
   * get context value by key
   *
   * @param key key
   * @return context value
   */
  @Nullable
  Object get(@Nonnull String key);

  /**
   * require context value by key
   *
   * @param key key
   * @return context value
   * @throws IllegalStateException if context not found
   */
  @Nonnull
  Object require(@Nonnull String key);

  /**
   * set context value by key and value
   *
   * @param key   key
   * @param value context value
   */
  void set(@Nonnull String key, @Nullable Object value);

  /**
   * remove context value by key
   *
   * @param key key
   * @return context value if contains, otherwise null
   */
  @Nullable
  Object remove(@Nonnull String key);

  /**
   * get is context value exist by key
   *
   * @param key key
   * @return is context value exist
   */
  boolean contains(@Nonnull String key);

  /**
   * get all context content
   *
   * @return all context content
   */
  @Nonnull
  Map<String, Object> content();

  /**
   * script context factory
   */
  interface Factory {
    /**
     * create builder instance
     *
     * @return builder instance
     */
    @Nonnull
    Builder builder();

    /**
     * create a empty instance
     *
     * @return empty instance
     */
    @Nonnull
    default ScriptContext empty() {
      return ScriptContext.builder().build();
    }
  }

  /**
   * script context builder
   */
  interface Builder extends IBuilder<Builder, ScriptContext> {
    /**
     * set context player
     *
     * @param player player instance
     * @return this
     */
    @Nonnull
    Builder player(@Nonnull Player player);

    /**
     * set context value by key
     *
     * @param key   key
     * @param value context value
     * @return this
     */
    @Nonnull
    Builder with(@Nonnull String key, @Nullable Object value);
  }

  class $internal$actions$ {
    private static final DcLazy<Factory> FACTORY = IocLazy.of(Factory.class);
  }
}
