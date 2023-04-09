package org.inksnow.ankh.jsnashorn;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;

@PluginModule
public class AnkhJsNashornModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(AnkhScriptEngine.class).annotatedWith(Names.named("js")).to(JsNashornEngine.class);
    bind(AnkhScriptEngine.class).annotatedWith(Names.named("nashorn")).to(JsNashornEngine.class);
  }
}
