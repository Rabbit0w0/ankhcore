package org.inksnow.ankh.core.script.engine.bsh;

import bsh.Interpreter;
import lombok.AllArgsConstructor;
import lombok.val;
import org.inksnow.ankh.core.api.script.PreparedScript;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.engine.console.ScriptLoggerConsole;
import org.inksnow.ankh.core.script.engine.console.ScriptPlayerConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

@AllArgsConstructor
public class BshPreparedScript implements PreparedScript {
  private static final Logger logger = LoggerFactory.getLogger("ankh-bsh");
  private final String shell;

  @Override
  public Object execute(@Nonnull ScriptContext context) throws Exception {
    val interpreter = new Interpreter();
    val player = context.player();
    interpreter.setClassLoader(this.getClass().getClassLoader());
    interpreter.setConsole(player == null ? new ScriptLoggerConsole(logger) : new ScriptPlayerConsole(context.player()));
    for (Map.Entry<String, Object> entry : context.content().entrySet()) {
      interpreter.set(entry.getKey(), entry.getValue());
    }
    return interpreter.eval(shell);
  }
}
