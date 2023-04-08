package org.inksnow.ankh.neigeitems;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.inksnow.ankh.core.api.item.ItemFetcher;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;

@PluginModule
public class AnkhNeigeitemsModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ItemFetcher.class).annotatedWith(Names.named("neigeitems")).to(NeigeItemFetcher.class);
  }
}
