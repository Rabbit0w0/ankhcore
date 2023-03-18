package bot.inker.ankh.core.script.bsh

import bot.inker.ankh.core.common.linebuf.LineBufferingOutputStream
import bot.inker.ankh.core.common.linebuf.TextStream
import bsh.ConsoleInterface
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.io.PrintStream
import java.io.Reader

class BshPlayerConsole(
  private val player: Player,
) : ConsoleInterface {
  private val delegateOut = PrintStream(LineBufferingOutputStream(DelegateOut(), "\n", 8192, 52))
  private val delegateErr = PrintStream(LineBufferingOutputStream(DelegateErr(), "\n", 8192, 52))

  override fun getIn(): Reader = Reader.nullReader()
  override fun getOut(): PrintStream = delegateOut
  override fun getErr(): PrintStream = delegateErr

  override fun println(o: Any) = delegateOut.println(o)
  override fun print(o: Any) = delegateOut.print(o)
  override fun error(o: Any) = delegateErr.print(o)

  inner class DelegateOut : TextStream {
    override fun text(rawText: String) {
      val text = if (rawText.endsWith('\n')) {
        rawText.substring(0, rawText.length - 1)
      } else {
        rawText
      }
      if (text.isNotEmpty()) {
        player.sendMessage(Component.text().color(NamedTextColor.GREEN).content(text).build())
      }
    }

    override fun endOfStream(failure: Throwable?) {
      //
    }
  }

  inner class DelegateErr : TextStream {
    override fun text(rawText: String) {
      val text = if (rawText.endsWith('\n')) {
        rawText.substring(0, rawText.length - 1)
      } else {
        rawText
      }
      if (text.isNotEmpty()) {
        player.sendMessage(Component.text().color(NamedTextColor.RED).content(text).build())
      }
    }

    override fun endOfStream(failure: Throwable?) {
      //
    }
  }
}