package org.inksnow.ankh.core.common.dsl

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.inksnow.ankh.core.api.AnkhCore

fun CommandSender.executeReport(action: () -> Unit) {
  catchReport(this, action)
}

fun catchReport(sender: Any?, action: () -> Unit): Boolean {
  try {
    action()
    return true
  } catch (e: Exception) {
    if (sender !is CommandSender || sender is ConsoleCommandSender) {
      val logger by action.logger()
      logger.error("Failed to handle command", e)
      return false
    }
    try {
      val component = Component.text()

      component.append(Component.text("${AnkhCore.PLUGIN_ID} 执行时发生异常", NamedTextColor.RED))
        .append(Component.newline())
      component.append(Component.text("${e::class.simpleName}: ${e.message}", NamedTextColor.RED))

      for (element in e.stackTrace) {
        val codeSource = runCatching { Class.forName(element.className) }
          .getOrNull()
          ?.protectionDomain
          ?.codeSource
          ?: "未知"
        var elementName = "${element.className}#${element.methodName}"
        if (elementName.length > 52) {
          elementName = elementName.substring(elementName.length - 52, elementName.length)
        }
        component.append(Component.newline()).append(Component.text("- ", NamedTextColor.WHITE)).append(
          Component.text(elementName, NamedTextColor.RED).hoverEvent(
            Component.text()
              .append(Component.text(AnkhCore.PLUGIN_ID)).append(Component.text(" 异常堆栈", NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.text(element.toString(), NamedTextColor.WHITE)).append(Component.newline())
              .append(Component.newline())
              .append(Component.text("类名: ${element.className}", NamedTextColor.RED)).append(Component.newline())
              .append(Component.text("方法名: ${element.methodName}", NamedTextColor.RED)).append(Component.newline())
              .append(Component.text("文件名: ${element.fileName}", NamedTextColor.RED)).append(Component.newline())
              .append(Component.text("行号: ${element.lineNumber}", NamedTextColor.RED)).append(Component.newline())
              .append(Component.newline())
              .append(Component.text("代码来源: $codeSource", NamedTextColor.GRAY))
              .build()
          )
        )
      }

      sender.sendMessage(component)
    } catch (e: Exception) {
      val logger by action.logger()
      logger.error("Failed to handle command", e)
      logger.error("Failed to handle command", e)
      logger.error("Failed to report exception", e)
    }
    return false
  }
}