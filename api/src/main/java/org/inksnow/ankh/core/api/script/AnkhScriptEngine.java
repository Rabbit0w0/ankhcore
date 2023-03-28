package org.inksnow.ankh.core.api.script;

import javax.annotation.Nonnull;

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
  default @Nonnull Object execute(@Nonnull ScriptContext context, @Nonnull String script) throws Exception {
    return prepare(script).execute(context);
  }

  /**
   * prepare script with engine
   *
   * @param script script text
   * @return prepared script
   * @throws Exception exception when parse script
   */
  @Nonnull PreparedScript prepare(@Nonnull String script) throws Exception;
}
