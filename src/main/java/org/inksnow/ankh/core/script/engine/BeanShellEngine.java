package org.inksnow.ankh.core.script.engine;

import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.script.engine.bsh.BshPreparedScript;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
public class BeanShellEngine implements AnkhScriptEngine {
  @Override
  public @Nonnull PreparedScript prepare(@Nonnull String script) throws Exception {
    return new BshPreparedScript(script);
  }
}
