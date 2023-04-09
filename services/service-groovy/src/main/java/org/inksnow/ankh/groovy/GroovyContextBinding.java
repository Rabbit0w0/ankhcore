package org.inksnow.ankh.groovy;

import groovy.lang.Binding;
import org.inksnow.ankh.core.api.script.ScriptContext;

import java.util.Map;

public class GroovyContextBinding extends Binding {
  private ScriptContext context;

  public GroovyContextBinding() {
    this.context = ScriptContext.factory().empty();
  }

  public ScriptContext getContext() {
    return context;
  }

  public void setContext(ScriptContext context) {
    this.context = context;
  }

  @Override
  public Object getVariable(String name) {
    return context.get(name);
  }

  @Override
  public void setVariable(String name, Object value) {
    context.set(name, value);
  }

  @Override
  public void removeVariable(String name) {
    context.remove(name);
  }

  @Override
  public boolean hasVariable(String name) {
    return context.contains(name);
  }

  @Override
  public Map getVariables() {
    return context.content();
  }
}
