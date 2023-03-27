package org.inksnow.ankh.core.api.util;

import javax.annotation.Nonnull;

/**
 * All builder interface
 *
 * @param <T> builder type
 * @param <R> build artificial type
 */
public interface IBuilder<T extends IBuilder<T, R>, R> {
  /**
   * builder instance
   *
   * @return this
   */
  @Nonnull
  T getThis();

  /**
   * build artificial
   *
   * @return instance built
   */
  @Nonnull
  R build();
}
