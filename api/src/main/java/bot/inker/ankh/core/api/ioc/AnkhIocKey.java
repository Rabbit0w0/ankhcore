package bot.inker.ankh.core.api.ioc;

import bot.inker.ankh.core.api.AnkhCore;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface AnkhIocKey<T> {
  static Factory factory() {
    return AnkhCore.getInstance(AnkhIocKey.Factory.class);
  }

  /**
   * Gets a key for an injection type.
   */
  static <T> AnkhIocKey<T> get(Class<T> type) {
    return factory().get(type);
  }

  /**
   * Gets a key for an injection type and an annotation type.
   */
  static <T> AnkhIocKey<T> get(Class<T> type, Class<? extends Annotation> annotationType) {
    return factory().get(type, annotationType);
  }

  /**
   * Gets a key for an injection type and an annotation.
   */
  static <T> AnkhIocKey<T> get(Class<T> type, Annotation annotation) {
    return factory().get(type, annotation);
  }

  /**
   * Gets a key for an injection type.
   */
  static AnkhIocKey<?> get(Type type) {
    return factory().get(type);
  }

  /**
   * Gets a key for an injection type and an annotation type.
   */
  static AnkhIocKey<?> get(Type type, Class<? extends Annotation> annotationType) {
    return factory().get(type, annotationType);
  }

  /**
   * Gets a key for an injection type and an annotation.
   */
  static AnkhIocKey<?> get(Type type, Annotation annotation) {
    return factory().get(type, annotation);
  }

  interface Factory {
    /**
     * Gets a key for an injection type.
     */
    <T> AnkhIocKey<T> get(Class<T> type);

    /**
     * Gets a key for an injection type and an annotation type.
     */
    <T> AnkhIocKey<T> get(Class<T> type, Class<? extends Annotation> annotationType);

    /**
     * Gets a key for an injection type and an annotation.
     */
    <T> AnkhIocKey<T> get(Class<T> type, Annotation annotation);

    /**
     * Gets a key for an injection type.
     */
    AnkhIocKey<?> get(Type type);

    /**
     * Gets a key for an injection type and an annotation type.
     */
    AnkhIocKey<?> get(Type type, Class<? extends Annotation> annotationType);

    /**
     * Gets a key for an injection type and an annotation.
     */
    AnkhIocKey<?> get(Type type, Annotation annotation);
  }
}
