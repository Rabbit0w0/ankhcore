package org.inksnow.ankh.jsnashorn;

import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.ScriptCacheStack;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.annotation.Nonnull;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public class JsNashornPreparedScript implements PreparedScript {
  private final String script;
  private final ScriptCacheStack<CompiledScript, ScriptException> cacheStack;

  public JsNashornPreparedScript(String script) throws ScriptException {
    this.script = script;
    this.cacheStack = new ScriptCacheStack<>(this::createScript);
    this.cacheStack.prepare(1);
  }

  private CompiledScript createScript() throws ScriptException {
    return createEngine().compile(script);
  }

  private NashornScriptEngine createEngine() {
    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    return (NashornScriptEngine) factory.getScriptEngine(
        new String[]{ "--global-per-engine" },
        this.getClass().getClassLoader()
    );
  }

  @Override
  public Object execute(@Nonnull ScriptContext context) throws Exception {
    CompiledScript compile = cacheStack.borrow();
    try {
      return compile.eval(new JsNashornScriptContext(context));
    } finally {
      cacheStack.sendBack(compile);
    }
  }
}
