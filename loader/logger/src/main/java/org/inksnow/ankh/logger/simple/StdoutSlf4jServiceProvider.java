package org.inksnow.ankh.logger.simple;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class StdoutSlf4jServiceProvider implements SLF4JServiceProvider {
  public static final String REQUESTED_API_VERSION = "2.0.99";
  private ILoggerFactory loggerFactory;
  private IMarkerFactory markerFactory;
  private MDCAdapter mdcAdapter;


  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return mdcAdapter;
  }

  @Override
  public String getRequestedApiVersion() {
    return REQUESTED_API_VERSION;
  }

  @Override
  public void initialize() {
    loggerFactory = new SimpleSlf4jLoggerFactory();
    markerFactory = new BasicMarkerFactory();
    mdcAdapter = new BasicMDCAdapter();
  }
}
