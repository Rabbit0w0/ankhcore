package org.inksnow.ankh.core.api.plugin;

import org.bukkit.event.EventPriority;

public interface AnkhPluginContainer {
  void callClinit();

  void callInit(AnkhBukkitPlugin bukkitPlugin);

  void callLoad();

  void callDisable();

  void callEnable();

  void onClinit(EventPriority priority, Runnable listener);

  void onInit(EventPriority priority, Runnable listener);

  void onLoad(EventPriority priority, Runnable listener);

  void onEnable(EventPriority priority, Runnable listener);

  void onDisable(EventPriority priority, Runnable listener);

  AnkhBukkitPlugin plugin();
}
