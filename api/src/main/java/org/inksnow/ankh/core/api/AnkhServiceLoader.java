package org.inksnow.ankh.core.api;

import net.kyori.adventure.key.Key;
import org.inksnow.ankh.core.api.ioc.IocLazy;
import org.inksnow.ankh.core.api.util.DcLazy;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Ankh service loader, as based named ioc
 */
public interface AnkhServiceLoader {
  /**
   * get this instance
   *
   * @return AnkhServiceLoader instance
   * @deprecated use static entry directly
   */
  @Deprecated
  static @Nonnull AnkhServiceLoader instance() {
    return $internal$actions$.INSTANCE.get();
  }

  /**
   * register a service by key
   *
   * @param key          the service key
   * @param serviceClass the service class
   * @param instance     the service instance
   * @param <T>          the service type (same as the service class)
   */
  static <T> void registerService(@Nonnull Key key, @Nonnull Class<T> serviceClass, @Nonnull T instance) {
    instance().registerServiceImpl(key, serviceClass, instance);
  }

  /**
   * get a service by key
   *
   * @param key   the service key (will get in all namespace)
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance
   * @throws IllegalStateException if the service not found
   */
  static <T> @Nonnull T loadService(@Nonnull String key, @Nonnull Class<T> clazz) {
    return instance().loadServiceImpl(key, clazz);
  }

  /**
   * get a service by key
   *
   * @param key   the service key
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance
   * @throws IllegalStateException if the service not found
   */
  static <T> @Nonnull T loadService(@Nonnull Key key, @Nonnull Class<T> clazz) {
    return instance().loadServiceImpl(key, clazz);
  }

  /**
   * get a service from config
   *
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance
   * @throws IllegalStateException if the service not found
   */
  static <T> @Nonnull T service(@Nonnull Class<T> clazz) {
    return instance().serviceImpl(clazz);
  }

  /**
   * get service list from config
   *
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance list
   * @throws IllegalStateException if the service not found
   */
  static <T> @Nonnull List<T> serviceList(@Nonnull Class<T> clazz) {
    return instance().serviceListImpl(clazz);
  }

  /**
   * register a service by key
   *
   * @param key          the service key
   * @param serviceClass the service class
   * @param instance     the service instance
   * @param <T>          the service type (same as the service class)
   */
  <T> void registerServiceImpl(@Nonnull Key key, @Nonnull Class<T> serviceClass, T instance);

  /**
   * get a service by key
   *
   * @param key   the service key (will get in all namespace)
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance
   * @throws IllegalStateException if the service not found
   */
  <T> T loadServiceImpl(@Nonnull String key, @Nonnull Class<T> clazz);

  /**
   * get a service by key
   *
   * @param key   the service key
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance
   * @throws IllegalStateException if the service not found
   */
  <T> T loadServiceImpl(@Nonnull Key key, @Nonnull Class<T> clazz);

  /**
   * get a service from config
   *
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance
   * @throws IllegalStateException if the service not found
   */
  <T> @Nonnull T serviceImpl(@Nonnull Class<T> clazz);

  /**
   * get service list from config
   *
   * @param clazz the service class
   * @param <T>   the service type (same as the service class)
   * @return the service instance list
   * @throws IllegalStateException if the service not found
   */
  <T> @Nonnull List<T> serviceListImpl(@Nonnull Class<T> clazz);

  public static class $internal$actions$ {
    private static DcLazy<AnkhServiceLoader> INSTANCE = IocLazy.of(AnkhServiceLoader.class);
  }
}
