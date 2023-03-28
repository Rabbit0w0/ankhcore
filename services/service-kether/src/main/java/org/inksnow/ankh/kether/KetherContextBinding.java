package org.inksnow.ankh.kether;

import org.inksnow.ankh.core.api.script.ScriptContext;
import org.jetbrains.annotations.NotNull;
import taboolib.library.kether.ParsedAction;
import taboolib.library.kether.Quest;
import taboolib.library.kether.QuestContext;
import taboolib.library.kether.QuestFuture;
import taboolib.module.kether.ScriptService;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class KetherContextBinding {
  private final ScriptContext[] contextRef;
  private final AnkhBindingScriptContext contextBinding;

  public KetherContextBinding(@Nonnull ScriptService service, @Nonnull Quest script) {
    this.contextRef = new ScriptContext[1];
    this.contextBinding = new AnkhBindingScriptContext(service, script);
  }

  public ScriptContext context() {
    return contextRef[0];
  }

  public void context(ScriptContext context){
    contextRef[0] = context;
  }

  public AnkhBindingScriptContext contextBinding() {
    return contextBinding;
  }

  public static class AnkhVarTable implements QuestContext.VarTable {
    private final QuestContext.Frame parent;
    private final ScriptContext[] contextRef;


    public AnkhVarTable(QuestContext.Frame parent, ScriptContext[] contextRef) {
      this.parent = parent;
      this.contextRef = contextRef;
    }

    @Override
    public QuestContext.VarTable parent() {
      return parent != null ? parent.variables() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(@NotNull String name) throws CompletionException {
      Object o = contextRef[0].get(name);
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
      Object o = contextRef[0].get(name);
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
        contextRef[0].set(name, value);
      } else {
        parent().set(name, value);
      }
    }

    @Override
    public <T> void set(@NotNull String name, @NotNull ParsedAction<T> owner, @NotNull CompletableFuture<T> future) {
      this.contextRef[0].set(name, new QuestFuture<>(owner, future));
    }

    @Override
    public void remove(@NotNull String name) {
      this.contextRef[0].remove(name);
    }

    @Override
    public void clear() {
      ScriptContext context = contextRef[0];
      for (String key : context.content().keySet()) {
        context.remove(key);
      }
    }

    @Override
    public Set<String> keys() {
      return Collections.unmodifiableSet(this.contextRef[0].content().keySet());
    }

    @Override
    public Collection<Map.Entry<String, Object>> values() {
      return Collections.unmodifiableCollection(this.contextRef[0].content().entrySet());
    }

    @Override
    public void initialize(@NotNull QuestContext.Frame frame) {
      for (Object o : this.contextRef[0].content().values()) {
        if (o instanceof QuestFuture) {
          ((QuestFuture<?>) o).run(frame);
        }
      }
    }

    @Override
    public void close() {
      for (Object o : this.contextRef[0].content().values()) {
        if (o instanceof QuestFuture) {
          ((QuestFuture<?>) o).close();
        }
      }
    }
  }

  public class AnkhBindingScriptContext extends taboolib.module.kether.ScriptContext {
    public AnkhBindingScriptContext(@NotNull ScriptService service, @NotNull Quest script) {
      super(service, script);
    }

    @Override
    protected Frame createRootFrame() {
      return new SimpleNamedFrame(null, new LinkedList<>(), new AnkhVarTable(null, contextRef), QuestContext.BASE_BLOCK, this);
    }
  }
}
