package org.inksnow.ankh.core.script.engine;

import bsh.Interpreter;
import lombok.val;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.engine.bsh.BshLoggerConsole;
import org.inksnow.ankh.core.script.engine.bsh.BshPlayerConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class BeanShellEngine implements AnkhScriptEngine {
  private static final Logger logger = LoggerFactory.getLogger("ankh-bsh");

  @Override
  public Object execute(ScriptContext context, String script) throws Exception {
    val interpreter = new Interpreter();
    val player = context.player();
    interpreter.setClassLoader(this.getClass().getClassLoader());
    interpreter.setConsole(player == null ? new BshLoggerConsole(logger) : new BshPlayerConsole(context.player()));
    for (Map.Entry<String, Object> entry : context.content().entrySet()) {
      interpreter.set(entry.getKey(), entry.getValue());
    }
    return interpreter.eval(script);
  }
}
