package org.inksnow.ankh.core.api.ioc;

import javax.inject.Provider;

/**
 * Builds the graphs of objects that make up your application. The injector tracks the dependencies
 * for each type and uses bindings to inject them. This is the core of Guice, although you rarely
 * interact with it directly. This "behind-the-scenes" operation is what distinguishes dependency
 * injection from its cousin, the service locator pattern.
 *
 * <p>Contains several default bindings:
 *
 * <ul>
 *   <li>This {@link AnkhInjector} instance itself
 *   <li>A {@code Provider<T>} for each binding of type {@code T}
 *   <li>The {@link java.util.logging.Logger} for the class being injected
 * </ul>
 *
 * <p>An injector can also {@link #injectMembers(Object) inject the dependencies} of
 * already-constructed instances. This can be used to interoperate with objects created by other
 * frameworks or services.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
public interface AnkhInjector {

  /**
   * Injects dependencies into the fields and methods of {@code instance}. Ignores the presence or
   * absence of an injectable constructor.
   *
   * <p>Whenever Guice creates an instance, it performs this injection automatically (after first
   * performing constructor injection), so if you're able to let Guice create all your objects for
   * you, you'll never need to use this method.
   *
   * @param instance to inject members on
   */
  void injectMembers(Object instance);

  /**
   * Returns the provider used to obtain instances for the given injection key. When feasible, avoid
   * using this method, in favor of having Guice inject your dependencies ahead of time.
   */
  <T> Provider<T> getProvider(AnkhIocKey<T> key);

  /**
   * Returns the provider used to obtain instances for the given type. When feasible, avoid using
   * this method, in favor of having Guice inject your dependencies ahead of time.
   */
  <T> Provider<T> getProvider(Class<T> type);

  /**
   * Returns the appropriate instance for the given injection key; equivalent to {@code
   * getProvider(key).get()}. When feasible, avoid using this method, in favor of having Guice
   * inject your dependencies ahead of time.
   */
  <T> T getInstance(AnkhIocKey<T> key);

  /**
   * Returns the appropriate instance for the given injection type; equivalent to {@code
   * getProvider(type).get()}. When feasible, avoid using this method, in favor of having Guice
   * inject your dependencies ahead of time.
   */
  <T> T getInstance(Class<T> type);

  /**
   * Returns this injector's parent, or {@code null} if this is a top-level injector.
   */
  AnkhInjector getParent();
}
