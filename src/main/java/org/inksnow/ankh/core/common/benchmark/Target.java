package org.inksnow.ankh.core.common.benchmark;

@FunctionalInterface
public interface Target<T, R> {
  R run(final T input);
  static <T,R> Target<T,R> runnable(final Runnable target){
    return it->{
      target.run();
      return null;
    };
  }
}
