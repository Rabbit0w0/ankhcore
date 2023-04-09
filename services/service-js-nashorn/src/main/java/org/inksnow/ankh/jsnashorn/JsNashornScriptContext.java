package org.inksnow.ankh.jsnashorn;

import bsh.ConsoleInterface;
import org.bukkit.entity.Player;
import org.inksnow.ankh.core.script.engine.console.ScriptLoggerConsole;
import org.inksnow.ankh.core.script.engine.console.ScriptPlayerConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class JsNashornScriptContext implements ScriptContext {
  private static final Logger logger = LoggerFactory.getLogger("ankh-js-nashorn");
  private static final List<Integer> scopes = List.of(ENGINE_SCOPE, GLOBAL_SCOPE);
  protected Writer writer;
  protected Writer errorWriter;
  protected Reader reader;
  protected Bindings engineScope;
  protected Bindings globalScope;

  public JsNashornScriptContext(org.inksnow.ankh.core.api.script.ScriptContext delegate) {
    Player player = delegate.player();
    ConsoleInterface console;
    if (player == null) {
      console = new ScriptLoggerConsole(logger);
    } else {
      console = new ScriptPlayerConsole(player);
    }
    this.writer = new OutputStreamWriter(console.getOut());
    this.errorWriter = new OutputStreamWriter(console.getErr());
    this.reader = console.getIn();
    this.engineScope = new SimpleBindings(delegate.content());
    this.globalScope = null;
  }

  public void setBindings(Bindings bindings, int scope) {
    switch (scope) {
      case ENGINE_SCOPE:
        if (bindings == null) {
          throw new NullPointerException("Engine scope cannot be null.");
        }
        engineScope = bindings;
        break;
      case GLOBAL_SCOPE:
        globalScope = bindings;
        break;
      default:
        throw new IllegalArgumentException("Invalid scope value.");
    }
  }

  public Object getAttribute(String name) {
    if (engineScope.containsKey(name)) {
      return getAttribute(name, ENGINE_SCOPE);
    } else if (globalScope != null && globalScope.containsKey(name)) {
      return getAttribute(name, GLOBAL_SCOPE);
    }

    return null;
  }

  public Object getAttribute(String name, int scope) {
    switch (scope) {

      case ENGINE_SCOPE:
        return engineScope.get(name);

      case GLOBAL_SCOPE:
        if (globalScope != null) {
          return globalScope.get(name);
        }
        return null;

      default:
        throw new IllegalArgumentException("Illegal scope value.");
    }
  }

  public Object removeAttribute(String name, int scope) {
    switch (scope) {

      case ENGINE_SCOPE:
        if (getBindings(ENGINE_SCOPE) != null) {
          return getBindings(ENGINE_SCOPE).remove(name);
        }
        return null;

      case GLOBAL_SCOPE:
        if (getBindings(GLOBAL_SCOPE) != null) {
          return getBindings(GLOBAL_SCOPE).remove(name);
        }
        return null;

      default:
        throw new IllegalArgumentException("Illegal scope value.");
    }
  }

  public void setAttribute(String name, Object value, int scope) {
    switch (scope) {
      case ENGINE_SCOPE:
        engineScope.put(name, value);
        return;

      case GLOBAL_SCOPE:
        if (globalScope != null) {
          globalScope.put(name, value);
        }
        return;

      default:
        throw new IllegalArgumentException("Illegal scope value.");
    }
  }

  public Writer getWriter() {
    return writer;
  }

  public void setWriter(Writer writer) {
    this.writer = writer;
  }

  public Reader getReader() {
    return reader;
  }

  public void setReader(Reader reader) {
    this.reader = reader;
  }

  public Writer getErrorWriter() {
    return errorWriter;
  }

  public void setErrorWriter(Writer writer) {
    this.errorWriter = writer;
  }

  public int getAttributesScope(String name) {
    if (engineScope.containsKey(name)) {
      return ENGINE_SCOPE;
    } else if (globalScope != null && globalScope.containsKey(name)) {
      return GLOBAL_SCOPE;
    } else {
      return -1;
    }
  }

  public Bindings getBindings(int scope) {
    if (scope == ENGINE_SCOPE) {
      return engineScope;
    } else if (scope == GLOBAL_SCOPE) {
      return globalScope;
    } else {
      throw new IllegalArgumentException("Illegal scope value.");
    }
  }

  public List<Integer> getScopes() {
    return scopes;
  }
}
