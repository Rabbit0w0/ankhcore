package bot.inker.ankh.core.api.ioc;

import bot.inker.ankh.core.api.AnkhCore;

public class IocLazy<T> extends DcLazy<T>{
  private final Class<T> instanceClass;

  public IocLazy(final Class<T> instanceClass) {
    this.instanceClass = instanceClass;
  }

  @Override
  protected T initialize() throws Throwable {
    return AnkhCore.getInstance(instanceClass);
  }

  public static <T> DcLazy<T> of(Class<T> clazz){
    return new IocLazy<>(clazz);
  }
}
