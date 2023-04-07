package org.inksnow.ankh.core.script.engine.bsh;

import bsh.ConsoleInterface;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.inksnow.ankh.core.common.linebuf.LineBufferingOutputStream;
import org.inksnow.ankh.core.common.linebuf.TextStream;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.io.Reader;

public class BshPlayerConsole implements ConsoleInterface {
  private static final Reader nullReader = Reader.nullReader();
  private final Player player;
  private final PrintStream delegateOut;
  private final PrintStream delegateErr;


  public BshPlayerConsole(Player player) {
    this.player = player;
    this.delegateOut = new PrintStream(
        new LineBufferingOutputStream(
            new DelegateOut(NamedTextColor.GREEN),
            "\n",
            8192,
            52
        )
    );
    this.delegateErr = new PrintStream(
        new LineBufferingOutputStream(
            new DelegateOut(NamedTextColor.RED),
            "\n",
            8192,
            52
        )
    );
  }

  @Override
  public Reader getIn() {
    return nullReader;
  }

  @Override
  public PrintStream getOut() {
    return delegateOut;
  }

  @Override
  public PrintStream getErr() {
    return delegateErr;
  }

  @Override
  public void println(Object o) {
    delegateOut.println(o);
  }

  @Override
  public void print(Object o) {
    delegateOut.print(o);
  }

  @Override
  public void error(Object o) {
    delegateErr.println(o);
  }

  private class DelegateOut implements TextStream {
    private final NamedTextColor color;

    private DelegateOut(NamedTextColor color) {
      this.color = color;
    }

    @Override
    public void text(String rawText) {
      val text = rawText.endsWith("\n") ? rawText.substring(0, rawText.length() - 1) : rawText;
      if (!text.isEmpty()) {
        player.sendMessage(Component.text(text, color));
      }
    }

    @Override
    public void endOfStream(@Nullable Throwable failure) {
      //
    }
  }
}
