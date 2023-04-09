package org.inksnow.ankh.groovy;

import groovy.lang.GroovyShell;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.ScriptCacheStack;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
public class GroovyEngine implements AnkhScriptEngine {
  private final GroovyShell publicGroovyShell = new GroovyShell(this.getClass().getClassLoader());
  private final ScriptCacheStack<GroovyShell, Exception> groovyShellCache = new ScriptCacheStack<>(() -> new GroovyShell(
      this.getClass().getClassLoader(),
      new GroovyContextBinding()
  ));

  @Override
  public @Nonnull Object execute(@Nonnull ScriptContext context, @Nonnull String script) throws Exception {
    GroovyShell groovyShell = groovyShellCache.borrow();
    try {
      ((GroovyContextBinding) groovyShell.getContext()).setContext(context);
      return groovyShell.evaluate(script);
    } finally {
      groovyShellCache.sendBack(groovyShell);
    }
  }

  @Override
  public @Nonnull PreparedScript prepare(@Nonnull String script) throws Exception {
    return new GroovyPreparedScript(publicGroovyShell, script);
  }
}
