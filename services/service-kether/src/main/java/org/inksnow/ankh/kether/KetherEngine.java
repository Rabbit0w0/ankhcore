package org.inksnow.ankh.kether;

import kotlin.Unit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.ScriptContext;
import taboolib.module.kether.KetherScriptLoader;
import taboolib.module.kether.ScriptService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

public class KetherEngine implements AnkhScriptEngine {
  private static final DcLazy<KetherEngine> INSTANCE = DcLazy.of((Supplier<KetherEngine>) KetherEngine::new);
  private final KetherScriptLoader scriptLoader = new KetherScriptLoader();

  private KetherEngine() {
    //
  }

  public static KetherEngine instance() {
    return INSTANCE.get();
  }

  @Override
  public Object execute(ScriptContext context, String rawScript) throws Exception {
    final var player = context.get("player");
    if (player instanceof CommandSender) {
      context.set("@Sender", player);
    } else {
      context.set("@Sender", Bukkit.getConsoleSender());
    }

    final var script = rawScript.startsWith("def ") ? rawScript : "def main = { " + rawScript + " }";

    final var quest = scriptLoader.load(
      ScriptService.INSTANCE,
      "temp_" + UUID.randomUUID(),
      script.getBytes(StandardCharsets.UTF_8)
    );

    final var rawResult = new KetherContextBinding(context, ScriptService.INSTANCE, quest)
      .contextBinding()
      .runActions()
      .get();
    return rawResult == Unit.INSTANCE ? null : rawResult;
  }
}
