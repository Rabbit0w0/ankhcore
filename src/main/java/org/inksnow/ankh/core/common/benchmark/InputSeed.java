package org.inksnow.ankh.core.common.benchmark;

@FunctionalInterface
public interface InputSeed<T> {
  T seed(int round);

  static <T> InputSeed<T> nop(){
    return it->null;
  }
}
