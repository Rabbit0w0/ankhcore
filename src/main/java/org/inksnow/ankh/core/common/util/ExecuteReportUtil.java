package org.inksnow.ankh.core.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.inksnow.ankh.core.api.AnkhCore;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.Callable;

@UtilityClass
@Slf4j
public class ExecuteReportUtil {
  public static final String UNKNOWN_MESSAGE = "unknown";

  public static <R> @Nullable Optional<R> catchReport(Object sender, Callable<R> action){
    try{
      return Optional.ofNullable(action.call());
    }catch (Exception e){
      reportForSender(sender, e);
      return null;
    }
  }

  public static void reportForSender(Object rawSender, Exception exception){
    try {
      if(!(rawSender instanceof CommandSender) || rawSender instanceof ConsoleCommandSender){
        logger.error("Failed to handle command", exception);
      }else{
        val sender = (CommandSender) rawSender;
        val component = Component.text();
        component.append(Component.text(AnkhCore.PLUGIN_ID + " Exception during execute", NamedTextColor.RED));
        component.append(Component.newline());
        component.append(Component.text(exception.getClass().getSimpleName() + ": " + exception.getMessage(), NamedTextColor.RED));
        for (StackTraceElement element : exception.getStackTrace()) {
          component.append(Component.newline());
          component.append(Component.text("- ", NamedTextColor.WHITE));
          component.append(Component.text(getElementName(element), NamedTextColor.RED)
            .hoverEvent(Component.text()
              .append(Component.text(AnkhCore.PLUGIN_ID))
              .append(Component.text(" StackTrace", NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.text(element.toString(), NamedTextColor.WHITE)).append(Component.newline())
              .append(Component.newline())
              .append(Component.text("ClassName: " + element.getClassName(), NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.text("MethodName: " + element.getMethodName(), NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.text("FileName: " + element.getFileName(), NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.text("LineNumber: " + element.getLineNumber(), NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.newline())
              .append(Component.text("CodeSource: "+getCodeSource(element.getClassName()), NamedTextColor.GRAY))
              .build()
          ));

          sender.sendMessage(component);
        }
      }
    }catch (Exception e){
      logger.error("Failed to handle command", e);
      logger.error("Failed to report exception", e);
    }
  }

  private static String getElementName(StackTraceElement element){
    val elementName = element.getClassName() + "#" + element.getMethodName();
    if(elementName.length() > 52){
      return elementName.substring(elementName.length() - 52);
    }else{
      return elementName;
    }
  }

  private static String getCodeSource(String className){
    try {
      return Class.forName(className)
        .getProtectionDomain()
        .getCodeSource()
        .toString();
    }catch (Exception e){
      return UNKNOWN_MESSAGE;
    }
  }
}
