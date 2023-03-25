package org.inksnow.ankh.core.api.plugin.annotations;

import org.bukkit.event.EventPriority;
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubscriptLifecycle {
  PluginLifeCycle value() default PluginLifeCycle.ENABLE;

  EventPriority priority() default EventPriority.NORMAL;
}
