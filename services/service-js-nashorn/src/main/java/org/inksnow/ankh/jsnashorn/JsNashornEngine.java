package org.inksnow.ankh.jsnashorn;

import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.PreparedScript;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
public class JsNashornEngine implements AnkhScriptEngine {
  @Override
  public @Nonnull PreparedScript prepare(@Nonnull String script) throws Exception {
    return new JsNashornPreparedScript(script);
  }
}
