package org.inksnow.ankh.core.script.engine.bsh;

import bsh.ConsoleInterface;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.inksnow.ankh.core.common.linebuf.LineBufferingOutputStream;
import org.inksnow.ankh.core.common.linebuf.TextStream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.PrintStream;
import java.io.Reader;

public class BshLoggerConsole implements ConsoleInterface {
  private final Logger logger;
  private final PrintStream delegateOut;
  private final PrintStream delegateErr;
  private static Reader nullReader = Reader.nullReader();

  public BshLoggerConsole(Logger logger) {
    this.logger = logger;
    this.delegateOut = new PrintStream(
      new LineBufferingOutputStream(
        new DelegateOut(false),
        "\n",
        8192,
        52
      )
    );
    this.delegateErr = new PrintStream(
      new LineBufferingOutputStream(
        new DelegateOut(true),
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
    private final boolean isError;
    private final boolean enabled;

    private DelegateOut(boolean isError) {
      this.isError = isError;
      if (isError){
        enabled = logger.isErrorEnabled();
      }else{
        enabled = logger.isInfoEnabled();
      }
    }

    @Override
    public void text(String rawText) {
      if(enabled){
        val text = rawText.endsWith("\n") ? rawText.substring(0, rawText.length() - 1) : rawText;
        if(isError){
          logger.error("{}", text);
        }else{
          logger.info("{}", text);
        }
      }
    }

    @Override
    public void endOfStream(@Nullable Throwable failure) {
      //
    }
  }
}
