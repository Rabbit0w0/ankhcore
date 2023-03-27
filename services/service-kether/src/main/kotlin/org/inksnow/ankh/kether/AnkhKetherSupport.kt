package org.inksnow.ankh.kether

import net.kyori.adventure.key.Key
import org.inksnow.ankh.core.api.AnkhServiceLoader
import org.inksnow.ankh.core.api.script.AnkhScriptEngine
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.kether.Kether

object AnkhKetherSupport {
  @Awake(LifeCycle.LOAD)
  fun load() {
    Kether.isAllowToleranceParser = true
    AnkhServiceLoader.registerService(
      Key.key("ankh-kether", "kether"),
      AnkhScriptEngine::class.java,
      KetherEngine.instance()
    )
  }
}