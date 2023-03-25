package org.inksnow.ankh.core.api.ioc;

import org.inksnow.ankh.core.api.AnkhCore;

public class IocLazy<T> extends DcLazy<T> {
  private final Class<T> instanceClass;

  public IocLazy(final Class<T> instanceClass) {
    this.instanceClass = instanceClass;
  }

  public static <T> DcLazy<T> of(Class<T> clazz) {
    return new IocLazy<>(clazz);
  }

  @Override
  protected T initialize() throws Throwable {
    return AnkhCore.getInstance(instanceClass);
  }
}
