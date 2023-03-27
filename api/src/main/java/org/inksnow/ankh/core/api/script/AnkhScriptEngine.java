package org.inksnow.ankh.core.api.script;

/**
 * Ankh script engine can execure script
 */
public interface AnkhScriptEngine {
  /**
   * execute script with engine
   *
   * @param context script context
   * @param script script text
   * @return script result
   *
   * @throws Exception exception when run script
   */
  Object execute(ScriptContext context, String script) throws Exception;
}
