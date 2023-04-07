package org.inksnow.ankh.core.script;

import java.util.Stack;

/**
 * A util to create script instance to reuse them
 * `borrow` will get a script instance, and create new one if no exist
 * `sendBack` will send a instance to stack
 * <code>
 * T instance = stack.borrow();
 * try{
 * // do something with instance
 * }finally{
 * stack.sendBack(instance);
 * }
 * </code>
 *
 * @param <T> script instance type
 * @param <E> exception when create script instance
 */
public class ScriptCacheStack<T, E extends Throwable> {
  private final FastJuStack<T> juStack = new FastJuStack<>();
  private final CreateInterface<T, E> supplier;

  public ScriptCacheStack(CreateInterface<T, E> supplier) {
    this.supplier = supplier;
  }

  public void sendBack(T o) {
    juStack.push(o);
  }

  public T borrow() throws E {
    T result = juStack.pop();
    if (result == null) {
      return supplier.create();
    } else {
      return result;
    }
  }

  public void prepare(int count) throws E {
    for (int i = 0; i < count; i++) {
      sendBack(supplier.create());
    }
  }

  @FunctionalInterface
  public interface CreateInterface<T, E extends Throwable> {
    T create() throws E;
  }

  private static class FastJuStack<T> extends Stack<T> {
    @Override
    public synchronized T pop() {
      T obj;
      int len = size();

      obj = peek();
      if (obj == null) {
        return null;
      }
      removeElementAt(len - 1);

      return obj;
    }

    @Override
    public synchronized T peek() {
      int len = size();

      if (len == 0) {
        return null;
      }

      return elementAt(len - 1);
    }
  }
}
