package org.inksnow.ankh.core.api.script;

import javax.annotation.Nonnull;

/**
 * prepared script, reduce time in parse script
 */
public interface PreparedScript {
  /**
   * execute prepared script
   *
   * @param context script context
   * @return script result
   * @throws Exception exception when run prepared script
   */
  Object execute(@Nonnull ScriptContext context) throws Exception;
}
