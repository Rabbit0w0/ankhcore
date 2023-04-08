package org.inksnow.ankh.core.api.script;

import org.bukkit.entity.Player;
import org.inksnow.ankh.core.api.ioc.IocLazy;
import org.inksnow.ankh.core.api.util.DcLazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ankh Script Service provide script engine and run shell
 * <p>
 * engine is load by AnkhServiceLoader, shell can special engine by prefix ':'
 * if you want to use groovy, can use ':groovy 1+1', or ':ankh-core:groovy 1+1'
 * <p>
 * if no prefix ':', it will use default script engine
 *
 * @see org.inksnow.ankh.core.api.AnkhServiceLoader
 */
public interface AnkhScriptService {
  /**
   * get ankh script service instance
   *
   * @return the instance
   */
  static @Nonnull AnkhScriptService instance() {
    return $internal$actions$.INSTANCE.get();
  }

  /**
   * get ankh script engine by key
   * if key non-null, same as <code>AnkhServiceLoader.loadService(key, AnkhScriptEngine.class);</code>
   * if key is null, same as <code>AnkhScriptService#get()</code>
   *
   * @param key the engine key
   * @return the engine instance
   * @throws IllegalStateException if the service not found
   */
  @Nonnull
  AnkhScriptEngine engine(@Nullable String key);

  /**
   * get default ankh script engine
   *
   * @return default engine instance
   */
  @Nonnull
  AnkhScriptEngine get();

  /**
   * run shell for player, will report result to player
   *
   * @param player player who use this shell
   * @param shell  shell as doc in class
   */
  void runPlayerShell(@Nonnull Player player, @Nonnull String shell);

  /**
   * run shell for console, will report result to console
   *
   * @param shell shell as doc in class
   */
  void runConsoleShell(@Nonnull String shell);

  /**
   * execute shell with context
   *
   * @param context script context
   * @param shell   shell as doc in class
   * @return shell result
   * @throws Exception exception when execute shell
   */
  @Nullable
  Object executeShell(@Nonnull ScriptContext context, @Nonnull String shell) throws Exception;

  /**
   * @param shell shell as doc in class
   * @return prepared script
   * @throws Exception exception when parse script
   */
  @Nonnull
  PreparedScript prepareShell(@Nonnull String shell) throws Exception;


  class $internal$actions$ {
    private static final DcLazy<AnkhScriptService> INSTANCE = IocLazy.of(AnkhScriptService.class);
  }
}
