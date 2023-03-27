package org.inksnow.ankh.core.script.engine;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.engine.groovy.GroovyContextBinding;

import javax.inject.Singleton;

@Singleton
public class GroovyEngine implements AnkhScriptEngine {
  @Override
  public Object execute(ScriptContext context, String script) throws Exception {
    return new GroovyShell(
      this.getClass().getClassLoader(),
      new GroovyContextBinding(context)
    ).evaluate(script);
  }
}
