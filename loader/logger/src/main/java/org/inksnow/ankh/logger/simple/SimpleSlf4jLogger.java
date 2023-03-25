package org.inksnow.ankh.logger.simple;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SimpleSlf4jLogger extends AbstractLogger {
  private static final boolean TRACE_ENABLE = getBooleanProperty("ankh.logger.simple.trace", false);
  private static final boolean DEBUG_ENABLE = getBooleanProperty("ankh.logger.simple.debug", false);
  private static final boolean INFO_ENABLE = getBooleanProperty("ankh.logger.simple.info", true);
  private static final boolean WARN_ENABLE = getBooleanProperty("ankh.logger.simple.warn", true);
  private static final boolean ERROR_ENABLE = getBooleanProperty("ankh.logger.simple.error", true);

  public SimpleSlf4jLogger(String name) {
    this.name = name;
  }

  private static boolean getBooleanProperty(String key, boolean defaultValue) {
    String value = System.getProperty(key);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value);
  }

  @Override
  protected String getFullyQualifiedCallerName() {
    return SimpleSlf4jLogger.class.getName();
  }

  @Override
  protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
    StringBuilder builder = new StringBuilder();
    builder.append("[").append(level).append("] ");
    if (marker != null) {
      builder.append("[").append(marker.getName()).append("]: ");
    }
    builder.append(MessageFormatter.basicArrayFormat(messagePattern, arguments));
    if (throwable != null) {
      StringWriter writer = new StringWriter();
      try (PrintWriter printWriter = new PrintWriter(writer)) {
        throwable.printStackTrace(printWriter);
      }
      builder.append('\n').append(writer);
    }
    System.out.println(builder);
  }

  @Override
  public boolean isTraceEnabled() {
    return TRACE_ENABLE;
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return TRACE_ENABLE;
  }

  @Override
  public boolean isDebugEnabled() {
    return DEBUG_ENABLE;
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return DEBUG_ENABLE;
  }

  @Override
  public boolean isInfoEnabled() {
    return INFO_ENABLE;
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return INFO_ENABLE;
  }

  @Override
  public boolean isWarnEnabled() {
    return WARN_ENABLE;
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return WARN_ENABLE;
  }

  @Override
  public boolean isErrorEnabled() {
    return ERROR_ENABLE;
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return ERROR_ENABLE;
  }
}
