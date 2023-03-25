package org.inksnow.ankh.logger;

import org.inksnow.ankh.logger.delegate.DelegateLoggerFactory;
import org.inksnow.ankh.logger.simple.StdoutSlf4jServiceProvider;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import java.lang.reflect.InvocationTargetException;

public class AnkhSlf4jServiceProvider implements SLF4JServiceProvider {
  private static final String[] PROVIDER_CLASSES = new String[]{
    "org.apache.logging.slf4j.SLF4JServiceProvider",
    "org.slf4j.jul.JULServiceProvider"
  };

  private final SLF4JServiceProvider delegate = tryDelegate();
  private ILoggerFactory loggerFactory;
  private IMarkerFactory markerFactory;
  private MDCAdapter mdcAdapter;

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> RuntimeException uncheck(Throwable e) throws T {
    throw (T) e;
  }

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
    return delegate.getRequestedApiVersion();
  }

  @Override
  public void initialize() {
    loggerFactory = new DelegateLoggerFactory(delegate.getLoggerFactory());
    markerFactory = delegate.getMarkerFactory();
    mdcAdapter = delegate.getMDCAdapter();
  }

  private SLF4JServiceProvider tryDelegate() {
    for (String providerClass : PROVIDER_CLASSES) {
      Class<? extends SLF4JServiceProvider> clazz;
      try {
        clazz = (Class<? extends SLF4JServiceProvider>) Class.forName(providerClass);
      } catch (ClassNotFoundException e) {
        continue;
      }
      try {
        SLF4JServiceProvider instance = clazz.getConstructor().newInstance();
        instance.initialize();
        return instance;
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        //
      }
    }
    System.err.println("No support logger api found, use simple fallback version");
    return new StdoutSlf4jServiceProvider();
  }
}
