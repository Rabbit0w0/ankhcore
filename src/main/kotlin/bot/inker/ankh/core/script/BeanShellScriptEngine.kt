package bot.inker.ankh.core.script

import bot.inker.ankh.core.api.AnkhCoreLoader
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent
import bot.inker.ankh.core.api.script.AnkhScriptEngine
import bot.inker.ankh.core.common.dsl.executeReport
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.script.bsh.BshPlayerConsole
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeanShellScriptEngine @Inject private constructor(
  private val coreLoader: AnkhCoreLoader,
) : AnkhScriptEngine {
  private val logger by logger()

  @SubscriptEvent(ignoreCancelled = true)
  private fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
    event.isCancelled = true
    if (!event.message.startsWith("!")) {
      return
    }
    val player = event.player
    val message = event.message
    Bukkit.getScheduler().runTask(coreLoader, Runnable {
      if (player.isOnline && (player.isOp || player.hasPermission("ankh.exec_script"))) {
        player.executeReport {
          runPlayerCommand(player, message.substring(1))
        }
      } else {
        event.player.sendMessage(
          Component.text().color(NamedTextColor.RED).content("You don't have enough permission to run shell command")
            .build()
        )
      }
    })
  }

  private fun runPlayerCommand(player: Player, command: String) {
    val console = BshPlayerConsole(player)
    val interpreter = bsh.Interpreter()
    interpreter.setClassLoader(this::class.java.classLoader)
    interpreter.setConsole(console)
    interpreter.set("player", player)
    val result = interpreter.eval(command)
    if (result != null) {
      console.out.println(result)
    }
    console.out.close()
    console.err.close()
  }
}