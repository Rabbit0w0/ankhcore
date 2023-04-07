package org.inksnow.ankh.core.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.inksnow.ankh.core.api.ioc.AnkhInjector;
import org.inksnow.ankh.core.api.ioc.AnkhIocKey;

import javax.inject.Provider;

public class AnkhCore {
  public static final String PLUGIN_ID = "ankh-core";
  public static final String PLUGIN_NAME = "Ankh Core";
  public static final Component PLUGIN_NAME_COMPONENT = Component.text()
      .append(Component.text("Ankh", NamedTextColor.YELLOW))
      .append(Component.text("Core", NamedTextColor.GREEN))
      .build();
  private static AnkhInjector injector;

  public static AnkhInjector getInjector() {
    return injector;
  }

  public static void injectMembers(Object instance) {
    injector.injectMembers(instance);
  }

  public static <T> Provider<T> getProvider(AnkhIocKey<T> key) {
    return injector.getProvider(key);
  }

  public static <T> Provider<T> getProvider(Class<T> type) {
    return injector.getProvider(type);
  }

  public static <T> T getInstance(AnkhIocKey<T> key) {
    return injector.getInstance(key);
  }

  public static <T> T getInstance(Class<T> type) {
    return injector.getInstance(type);
  }

  public static class $internal$actions$ {
    public static void setInjector(AnkhInjector injector) {
      AnkhCore.injector = injector;
    }
  }
}
