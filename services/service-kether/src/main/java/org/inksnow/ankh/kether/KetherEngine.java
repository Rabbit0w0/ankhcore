package org.inksnow.ankh.kether;

import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.PreparedScript;
import taboolib.module.kether.KetherScriptLoader;
import taboolib.module.kether.ScriptService;

import javax.annotation.Nonnull;
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
  public @Nonnull PreparedScript prepare(@Nonnull String shell) throws Exception {
    final var script = shellToScript(shell);

    final var quest = scriptLoader.load(
      ScriptService.INSTANCE,
      "temp_" + UUID.randomUUID(),
      script.getBytes(StandardCharsets.UTF_8)
    );

    return new KetherPreparedScript(quest);
  }

  private static @Nonnull String shellToScript(@Nonnull String shell){
    return shell.startsWith("def ") ? shell : "def main = { " + shell + " }";
  }
}
