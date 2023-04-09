package org.inksnow.ankh.jsnashorn;

import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.api.util.DcLazy;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
public class JsNashornEngine implements AnkhScriptEngine {
  private final DcLazy<NashornScriptEngine> nashornScriptEngine = DcLazy.of(this::createEngine);

  private @Nonnull NashornScriptEngine createEngine() {
    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    return (NashornScriptEngine) factory.getScriptEngine(this.getClass().getClassLoader());
  }

  @Nonnull
  @Override
  public Object execute(@Nonnull ScriptContext context, @Nonnull String script) throws Exception {
    return nashornScriptEngine.get().eval(script, new JsNashornScriptContext(context));
  }

  @Override
  public @Nonnull PreparedScript prepare(@Nonnull String script) throws Exception {
    return new JsNashornPreparedScript(
        nashornScriptEngine.get().compile(script)
    );
  }
}
