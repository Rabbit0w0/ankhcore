package bot.inker.ankh.core;

import bot.inker.ankh.core.api.AnkhCoreLoader;
import bot.inker.ankh.core.api.block.BlockRegistry;
import bot.inker.ankh.core.api.hologram.HologramService;
import bot.inker.ankh.core.api.ioc.AnkhIocKey;
import bot.inker.ankh.core.api.item.AnkhItemRegistry;
import bot.inker.ankh.core.api.plugin.AnkhPluginManager;
import bot.inker.ankh.core.api.plugin.annotations.PluginModule;
import bot.inker.ankh.core.api.storage.ChunkStorage;
import bot.inker.ankh.core.api.storage.LocationStorage;
import bot.inker.ankh.core.api.world.WorldService;
import bot.inker.ankh.core.api.world.storage.BlockStorageEntry;
import bot.inker.ankh.core.api.world.storage.StorageBackend;
import bot.inker.ankh.core.block.BlockRegisterService;
import bot.inker.ankh.core.common.entity.LocationEmbedded;
import bot.inker.ankh.core.common.entity.WorldChunkEmbedded;
import bot.inker.ankh.core.hologram.HologramProvider;
import bot.inker.ankh.core.hologram.hds.HdsHologramService;
import bot.inker.ankh.core.hologram.nop.NopHologramService;
import bot.inker.ankh.core.ioc.BridgerKey;
import bot.inker.ankh.core.item.ItemRegisterService;
import bot.inker.ankh.core.plugin.AnkhPluginContainerImpl;
import bot.inker.ankh.core.plugin.AnkhPluginManagerImpl;
import bot.inker.ankh.core.world.AnkhWorldService;
import bot.inker.ankh.core.world.storage.BlockStorageEntryImpl;
import bot.inker.ankh.core.world.storage.DatabaseBackend;
import bot.inker.ankh.core.world.storage.FilesystemBackend;
import bot.inker.ankh.loader.AnkhCoreLoaderPlugin;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;

@PluginModule
@Slf4j
public class AnkhCorePluginModule extends AbstractModule {
  static {
    ScreenPrinter.print(logger);
  }

  @Override
  protected void configure() {
    bind(AnkhIocKey.Factory.class).to(BridgerKey.Factory.class);

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
