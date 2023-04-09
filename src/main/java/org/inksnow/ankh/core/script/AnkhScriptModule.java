package org.inksnow.ankh.core.script;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.api.script.AnkhScriptEngine;
import org.inksnow.ankh.core.api.script.AnkhScriptService;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.inksnow.ankh.core.script.engine.BeanShellEngine;

@PluginModule
public class AnkhScriptModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ScriptContext.Factory.class).to(ScriptContextImpl.Factory.class);

    bind(AnkhScriptService.class).to(ScriptServiceImpl.class);
    bind(AnkhScriptEngine.class).toProvider(ScriptServiceImpl.class);
    bind(AnkhScriptEngine.class).annotatedWith(Names.named("bsh")).to(BeanShellEngine.class);
  }
}
