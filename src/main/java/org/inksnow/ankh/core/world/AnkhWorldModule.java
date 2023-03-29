package org.inksnow.ankh.core.world;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.api.world.WorldService;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.api.world.storage.WorldStorage;
import org.inksnow.ankh.core.world.storage.BlockStorageEntryImpl;
import org.inksnow.ankh.core.world.storage.DatabaseWorldStorage;

@PluginModule
public class AnkhWorldModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(WorldStorage.class).annotatedWith(Names.named("database")).to(DatabaseWorldStorage.class);
    bind(WorldStorage.class).annotatedWith(Names.named("filesystem")).to(DatabaseWorldStorage.class);
    bind(WorldStorage.class).annotatedWith(Names.named("pdc")).to(DatabaseWorldStorage.class);

    bind(WorldService.class).to(PdcWorldService.class);
    bind(BlockStorageEntry.Factory.class).to(BlockStorageEntryImpl.Factory.class);
  }
}
