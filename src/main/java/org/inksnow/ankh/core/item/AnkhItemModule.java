package org.inksnow.ankh.core.item;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.SneakyThrows;
import org.inksnow.ankh.core.api.item.AnkhItemRegistry;
import org.inksnow.ankh.core.api.item.AnkhItemService;
import org.inksnow.ankh.core.api.item.ItemFetcher;
import org.inksnow.ankh.core.api.item.ItemTagger;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.item.fetcher.LoreItemFetcher;
import org.inksnow.ankh.core.item.fetcher.TagItemFetcher;
import org.inksnow.ankh.core.item.tagger.PdcItemTagger;

@PluginModule
public class AnkhItemModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(AnkhItemRegistry.class).to(ItemRegisterService.class);
    bind(AnkhItemService.class).to(AnkhItemServiceImpl.class);

    bind(ItemTagger.class).annotatedWith(Names.named("pdc")).to(PdcItemTagger.class);

    bind(ItemFetcher.class).annotatedWith(Names.named("tag")).to(TagItemFetcher.class);
    bind(ItemFetcher.class).annotatedWith(Names.named("lore")).to(LoreItemFetcher.class);
  }

  @SneakyThrows
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void bindIfExist(String requireClassName, Class<?> serviceClass, String name, String targetClass) {
    try {
      Class.forName(requireClassName);
    } catch (ClassNotFoundException e) {
      return;
    }
    bind(serviceClass).annotatedWith(Names.named(name)).to((Class) Class.forName(targetClass));
  }
}
