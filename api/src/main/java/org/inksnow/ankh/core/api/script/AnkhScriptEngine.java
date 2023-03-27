package org.inksnow.ankh.core.api.script;

public interface AnkhScriptEngine {
  Object execute(ScriptContext context, String script) throws Exception;
}
