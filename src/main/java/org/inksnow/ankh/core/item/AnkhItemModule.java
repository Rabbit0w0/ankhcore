package org.inksnow.ankh.core.item;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.inksnow.ankh.core.api.item.AnkhItemRegistry;
import org.inksnow.ankh.core.api.item.AnkhItemService;
import org.inksnow.ankh.core.api.item.ItemTagger;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.item.tagger.PdcItemTagger;

@PluginModule
public class AnkhItemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(AnkhItemRegistry.class).to(ItemRegisterService.class);
    bind(AnkhItemService.class).to(AnkhItemServiceImpl.class);

    bind(ItemTagger.class).annotatedWith(Names.named("pdc")).to(PdcItemTagger.class);
  }
}
