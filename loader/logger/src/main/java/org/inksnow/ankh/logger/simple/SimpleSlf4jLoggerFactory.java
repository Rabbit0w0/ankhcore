package org.inksnow.ankh.logger.simple;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleSlf4jLoggerFactory implements ILoggerFactory {
  private final ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

  @Override
  public Logger getLogger(String name) {
    return loggerMap.computeIfAbsent(name, SimpleSlf4jLogger::new);
  }
}
