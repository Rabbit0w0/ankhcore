package org.inksnow.ankh.core.script;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.inksnow.ankh.core.api.AnkhCoreLoader;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.AnkhScriptService;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.common.config.AnkhConfig;
import org.inksnow.ankh.core.common.util.ExecuteReportUtil;
import org.slf4j.helpers.MessageFormatter;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Singleton
@Slf4j
public class ScriptServiceImpl implements AnkhScriptService, Provider<AnkhScriptEngine> {
  private final AnkhCoreLoader coreLoader;
  private final AnkhConfig config;
  private final DcLazy<AnkhScriptEngine> defaultEngine = DcLazy.of((Supplier<AnkhScriptEngine>) this::defaultEngineImpl);
  private final Map<String, AnkhScriptEngine> engineMap = new ConcurrentSkipListMap<>();
  private final Function<String, AnkhScriptEngine> engineLoadFunction = this::loadEngineImpl;

  @Inject
  private ScriptServiceImpl(AnkhCoreLoader coreLoader, AnkhConfig config) {
    this.coreLoader = coreLoader;
    this.config = config;
  }

  public AnkhScriptEngine engine(@Nullable String key){
    if(key == null){
      return defaultEngine.get();
    }
    return engineMap.computeIfAbsent(key, engineLoadFunction);
  }

  private AnkhScriptEngine defaultEngineImpl() {
    val configDefaultService = config.getService().getScript();
    return engine(configDefaultService == null ? "ankh-core:bsh" : configDefaultService);
  }

  private AnkhScriptEngine loadEngineImpl(String key){
    return AnkhServiceLoader.loadService(key, AnkhScriptEngine.class);
  }

  @Override
  public AnkhScriptEngine get() {
    return defaultEngine.get();
  }

  public void runPlayerShell(Player player, String shell){
    String engineName;
    String command;
    if(shell.startsWith(":")){
      val firstSplit = shell.indexOf(' ');
      engineName = shell.substring(1, firstSplit == -1 ? shell.length() : firstSplit);
      command = firstSplit == -1 ? "" : shell.substring(firstSplit + 1);
    }else{
      engineName = null;
      command = shell;
    }

    long passTime;
    Object result;
    val engine = engineName == null ? get() : engine(engineName);
    try{
      val startTime = System.nanoTime();
      result = engine.execute(ScriptContext.builder().player(player).build(), command);
      passTime = System.nanoTime() - startTime;
    }catch (Exception e){
      ExecuteReportUtil.reportForSender(player, e);
      return;
    }
    val resultMessage = MessageFormatter.format("{}", result).getMessage();
    Component layoutComponent;
    if(resultMessage.length() > 45){
      layoutComponent = Component.text(resultMessage.substring(0, 42), NamedTextColor.WHITE)
        .append(Component.text("...", NamedTextColor.BLUE));
    }else {
      layoutComponent = Component.text(resultMessage, NamedTextColor.WHITE);
    }
    Component hoverComponent;
    if(resultMessage.length() > 1000){
      hoverComponent = Component.text(resultMessage.substring(0, 800), NamedTextColor.WHITE)
        .append(Component.newline())
        .append(Component.newline())
        .append(Component.text("time: ", NamedTextColor.GOLD))
        .append(Component.text(TimeUnit.NANOSECONDS.toMillis(passTime), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("type: ", NamedTextColor.GOLD))
        .append(Component.text(result == null ? "null" : result.getClass().getName()))
        .append(Component.newline())
        .append(Component.text("result length="+resultMessage.length()+", cut first 800 chars", NamedTextColor.GOLD));
    }else{
      hoverComponent = Component.text(resultMessage, NamedTextColor.WHITE)
        .append(Component.newline())
        .append(Component.newline())
        .append(Component.text("time: ", NamedTextColor.GOLD))
        .append(Component.text(TimeUnit.NANOSECONDS.toMillis(passTime), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("type: ", NamedTextColor.GOLD))
        .append(Component.text(result == null ? "null" : result.getClass().getName()));
    }
    player.sendMessage(Component.text("[result] ", NamedTextColor.GOLD)
      .append(layoutComponent.hoverEvent(hoverComponent)));
  }

  public void runConsoleShell(String shell){
    String engineName;
    String command;
    if(shell.startsWith(":")){
      val firstSplit = shell.indexOf(' ');
      engineName = shell.substring(1, firstSplit == -1 ? shell.length() : firstSplit);
      command = firstSplit == -1 ? "" : shell.substring(firstSplit + 1);
    }else{
      engineName = null;
      command = shell;
    }

    val engine = engineName == null ? get() : engine(engineName);

    long passtime;

    Object result;
    try{
      val startTime = System.nanoTime();
      result = engine.execute(ScriptContext.builder()
        .with("isConsole", true)
        .build(), command);
      passtime = System.nanoTime() - startTime;
    } catch (Exception e) {
      logger.error("Failed to run console shell", e);
      return;
    }

    logger.info("[result] class={}, time={}ms", result.getClass().getName(), TimeUnit.NANOSECONDS.toMillis(passtime));
    logger.info("[result] {}", result);
  }

  @SubscriptEvent(priority = EventPriority.LOW, ignoreCancelled = true)
  private void onServerCommand(ServerCommandEvent event){
    val rawCommand = event.getCommand();
    if (rawCommand.startsWith(config.getPlayerShell().getPrefix())) {
      event.setCancelled(true);
      runConsoleShell(rawCommand.substring(config.getPlayerShell().getPrefix().length()));
    }
  }

  @SubscriptEvent(priority = EventPriority.LOW, ignoreCancelled = true)
  private void onAsyncChat(AsyncChatEvent event){
    if(!config.getPlayerShell().getEnabled()){
      return;
    }
    val message = PlainComponentSerializer.plain().serialize(event.originalMessage());
    if(message.startsWith(config.getPlayerShell().getPrefix())){
      event.setCancelled(true);
      val player = event.getPlayer();
      val command = message.substring(config.getPlayerShell().getPrefix().length());
      Bukkit.getScheduler().runTask(coreLoader, () -> {
        if (!player.isOnline()) {
          return;
        }
        if (player.isOp() || player.hasPermission("ankh.script.execute-any")) {
          runPlayerShell(player, command);
        }else{
          player.sendMessage(Component.text("Permission denied", NamedTextColor.RED));
        }
      });
    }
  }
}
