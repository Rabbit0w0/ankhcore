package org.inksnow.ankh.kether;

import org.inksnow.ankh.core.api.script.ScriptContext;
import org.jetbrains.annotations.NotNull;
import taboolib.library.kether.*;
import taboolib.module.kether.ScriptService;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class KetherContextBinding{
  private final ScriptContext context;
  private final AnkhBindingScriptContext contextBinding;

  public KetherContextBinding(@Nonnull ScriptContext context, @Nonnull ScriptService service, @Nonnull Quest script) {
    this.context = context;
    this.contextBinding = new AnkhBindingScriptContext(service, script);
  }

  public AnkhBindingScriptContext contextBinding() {
    return contextBinding;
  }

  public class AnkhBindingScriptContext extends taboolib.module.kether.ScriptContext {
    public AnkhBindingScriptContext(@NotNull ScriptService service, @NotNull Quest script) {
      super(service, script);
    }

    @Override
    protected Frame createRootFrame() {
      return new SimpleNamedFrame(null, new LinkedList<>(), new AnkhVarTable(null, context), QuestContext.BASE_BLOCK, this);
    }
  }

  public static class AnkhVarTable implements QuestContext.VarTable {
    private final QuestContext.Frame parent;
    private final ScriptContext context;

    public AnkhVarTable(QuestContext.Frame parent) {
      this(parent, ScriptContext.builder().build());
    }

    public AnkhVarTable(QuestContext.Frame parent, ScriptContext context) {
      this.parent = parent;
      this.context = context;
    }

    @Override
    public QuestContext.VarTable parent() {
      return parent != null ? parent.variables() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(@NotNull String name) throws CompletionException {
      Object o = context.get(name);
      if (o == null && parent != null) {
        return parent.variables().get(name);
      }
      if (o instanceof QuestFuture<?>) {
        o = ((QuestFuture<?>) o).getFuture().join();
      }
      return (Optional<T>) Optional.ofNullable(o);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<QuestFuture<T>> getFuture(@NotNull String name) {
      Object o = context.get(name);
      if (o == null && parent != null) {
        return parent.variables().getFuture(name);
      }
      if (o instanceof QuestFuture) {
        return Optional.of((QuestFuture<T>) o);
      } else {
        return Optional.empty();
      }
    }

    @Override
    public void set(@NotNull String name, Object value) {
      if (name.startsWith("~") || parent() == null) {
         context.set(name, value);
      } else {
        parent().set(name, value);
      }
    }

    @Override
    public <T> void set(@NotNull String name, @NotNull ParsedAction<T> owner, @NotNull CompletableFuture<T> future) {
      this.context.set(name, new QuestFuture<>(owner, future));
    }

    @Override
    public void remove(@NotNull String name) {
      this.context.remove(name);
    }

    @Override
    public void clear() {
      for (String key : this.context.content().keySet()) {
        this.context.remove(key);
      }
    }

    @Override
    public Set<String> keys() {
      return Collections.unmodifiableSet(this.context.content().keySet());
    }

    @Override
    public Collection<Map.Entry<String, Object>> values() {
      return Collections.unmodifiableCollection(this.context.content().entrySet());
    }

    @Override
    public void initialize(@NotNull QuestContext.Frame frame) {
      for (Object o : this.context.content().values()) {
        if (o instanceof QuestFuture) {
          ((QuestFuture<?>) o).run(frame);
        }
      }
    }

    @Override
    public void close() {
      for (Object o : this.context.content().values()) {
        if (o instanceof QuestFuture) {
          ((QuestFuture<?>) o).close();
        }
      }
    }
  }
}
