package org.inksnow.ankh.core.api.util;

import javax.annotation.Nonnull;

public interface IBuilder<T extends IBuilder<T, R>, R> {
  @Nonnull
  T getThis();

  @Nonnull
  R build();
}
