package org.inksnow.ankh.jsnashorn;

import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.api.script.ScriptContext;

import javax.annotation.Nonnull;
import javax.script.CompiledScript;

public class JsNashornPreparedScript implements PreparedScript {
  private final CompiledScript compiledScript;

  public JsNashornPreparedScript(CompiledScript compiledScript) {
    this.compiledScript = compiledScript;
  }

  @Override
  public Object execute(@Nonnull ScriptContext context) throws Exception {
    return compiledScript.eval(new JsNashornScriptContext(context));
  }
}
