package org.inksnow.ankh.core.item.nbtapi;

import lombok.experimental.UtilityClass;
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle;
import org.inksnow.ankh.core.libs.nbtapi.utils.MinecraftVersion;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@UtilityClass
class NbtApiConfig {
  @SubscriptLifecycle(PluginLifeCycle.LOAD)
  private static void configNbtApi() {
    MinecraftVersion.replaceLogger(new NbtApiLogger());
    MinecraftVersion.disableBStats();
    MinecraftVersion.disableUpdateCheck();
    MinecraftVersion.disablePackageWarning();
  }

  private static class NbtApiLogger extends Logger {
    protected NbtApiLogger() {
      super("ankh:nbtapi", null);
    }

    @Override
    public void log(LogRecord record) {
      if (record.getLevel().intValue() <= Level.INFO.intValue()) {
        record.setLevel(Level.CONFIG);
      }
      String message = record.getMessage();
      if (message.startsWith("[NBTAPI] ")) {
        record.setMessage(message.substring("[NBTAPI] ".length()));
      }
      super.log(record);
    }
  }
}
