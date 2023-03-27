package org.inksnow.ankh.core;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import org.inksnow.ankh.core.api.AnkhCoreLoader;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.block.BlockRegistry;
import org.inksnow.ankh.core.api.hologram.HologramService;
import org.inksnow.ankh.core.api.ioc.AnkhIocKey;
import org.inksnow.ankh.core.api.item.AnkhItemRegistry;
import org.inksnow.ankh.core.api.plugin.AnkhPluginManager;
import org.inksnow.ankh.core.api.plugin.annotations.PluginModule;
import org.inksnow.ankh.core.api.storage.ChunkStorage;
import org.inksnow.ankh.core.api.storage.LocationStorage;
import org.inksnow.ankh.core.api.world.WorldService;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.api.world.storage.StorageBackend;
import org.inksnow.ankh.core.block.BlockRegisterService;
import org.inksnow.ankh.core.common.AnkhServiceLoaderImpl;
import org.inksnow.ankh.core.common.entity.LocationEmbedded;
import org.inksnow.ankh.core.common.entity.WorldChunkEmbedded;
import org.inksnow.ankh.core.hologram.HologramProvider;
import org.inksnow.ankh.core.hologram.hds.HdsHologramService;
import org.inksnow.ankh.core.hologram.nop.NopHologramService;
import org.inksnow.ankh.core.ioc.BridgerKey;
import org.inksnow.ankh.core.item.ItemRegisterService;
import org.inksnow.ankh.core.plugin.AnkhPluginContainerImpl;
import org.inksnow.ankh.core.plugin.AnkhPluginManagerImpl;
import org.inksnow.ankh.core.world.AnkhWorldService;
import org.inksnow.ankh.core.world.storage.BlockStorageEntryImpl;
import org.inksnow.ankh.core.world.storage.DatabaseBackend;
import org.inksnow.ankh.core.world.storage.FilesystemBackend;
import org.inksnow.ankh.loader.AnkhCoreLoaderPlugin;

@PluginModule
@Slf4j
public class AnkhCorePluginModule extends AbstractModule {
  static {
    ScreenPrinter.print(logger);
  }

  @Override
  protected void configure() {
    bind(AnkhIocKey.Factory.class).to(BridgerKey.Factory.class);
    bind(AnkhServiceLoader.class).to(AnkhServiceLoaderImpl.class);

    bind(AnkhCoreLoader.class).toProvider(() -> (AnkhCoreLoader) ((AnkhPluginContainerImpl) AnkhCoreLoaderPlugin.container).getBukkitPlugin());

    bind(AnkhPluginManager.class).to(AnkhPluginManagerImpl.class);
    bind(AnkhPluginManagerImpl.class).toInstance(AnkhPluginManagerImpl.INSTANCE);

    bind(StorageBackend.class).annotatedWith(Names.named("database")).to(DatabaseBackend.class);
    bind(StorageBackend.class).annotatedWith(Names.named("filesystem")).to(FilesystemBackend.class);

    bind(LocationStorage.Factory.class).to(LocationEmbedded.Factory.class);
    bind(ChunkStorage.Factory.class).to(WorldChunkEmbedded.Factory.class);

    bind(HologramService.class).toProvider(HologramProvider.class);
    bind(HologramService.class).annotatedWith(Names.named("holographic-displays")).to(HdsHologramService.class);
    bind(HologramService.class).annotatedWith(Names.named("nop")).to(NopHologramService.class);

    bind(WorldService.class).to(AnkhWorldService.class);
    bind(BlockStorageEntry.Factory.class).to(BlockStorageEntryImpl.Factory.class);

    bind(BlockRegistry.class).to(BlockRegisterService.class);
    bind(AnkhItemRegistry.class).to(ItemRegisterService.class);
  }
}
