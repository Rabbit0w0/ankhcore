package org.inksnow.ankh.core.script.engine.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.val;
import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.ScriptCacheStack;

import javax.annotation.Nonnull;

public class GroovyPreparedScript implements PreparedScript {
  private final GroovyShell publicGroovyShell;
  private final String script;
  private final ScriptCacheStack<Script, Exception> localCache;

  public GroovyPreparedScript(GroovyShell publicGroovyShell, String script) throws Exception {
    this.publicGroovyShell = publicGroovyShell;
    this.script = script;
    this.localCache = new ScriptCacheStack<>(this::provideScript);
    localCache.prepare(1);
  }

  private Script provideScript() {
    return publicGroovyShell.parse(script, new GroovyContextBinding());
  }

  @Override
  public Object execute(@Nonnull ScriptContext context) throws Exception {
    val script = localCache.borrow();
    try {
      ((GroovyContextBinding) script.getBinding()).context(context);
      return script.run();
    } finally {
      localCache.sendBack(script);
    }
  }
}
