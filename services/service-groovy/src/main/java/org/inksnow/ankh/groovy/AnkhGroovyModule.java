package org.inksnow.ankh.groovy;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;

@PluginModule
public class AnkhGroovyModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(AnkhScriptEngine.class).annotatedWith(Names.named("groovy")).to(GroovyEngine.class);
  }
}
