package bot.inker.ankh.core.api.util;

public interface IBuilder<T extends IBuilder<T, R>, R> {
  T getThis();

  R build();
}
