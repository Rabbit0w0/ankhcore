package bot.inker.ankh.core

import bot.inker.ankh.core.api.AnkhCoreLoader
import bot.inker.ankh.core.api.block.BlockRegistry
import bot.inker.ankh.core.api.hologram.HologramService
import bot.inker.ankh.core.api.ioc.AnkhIocKey
import bot.inker.ankh.core.api.item.AnkhItemRegistry
import bot.inker.ankh.core.api.plugin.AnkhPluginManager
import bot.inker.ankh.core.api.plugin.annotations.PluginModule
import bot.inker.ankh.core.api.world.WorldService
import bot.inker.ankh.core.block.BlockRegisterService
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.hologram.HologramProvider
import bot.inker.ankh.core.ioc.BridgerKey
import bot.inker.ankh.core.item.ItemRegisterService
import bot.inker.ankh.core.plugin.AnkhPluginContainerImpl
import bot.inker.ankh.core.plugin.AnkhPluginManagerImpl
import bot.inker.ankh.core.world.AnkhWorldService
import bot.inker.ankh.core.world.storage.DatabaseBackend
import bot.inker.ankh.core.world.storage.FilesystemBackend
import bot.inker.ankh.core.world.storage.StorageBackend
import bot.inker.ankh.loader.AnkhCoreLoaderPlugin
import com.google.inject.AbstractModule
import com.google.inject.name.Names

@PluginModule
class AnkhCorePluginImpl : AbstractModule() {
  private val logger = logger().also(ScreenPrinter::print)

  override fun configure() {
    bind(AnkhIocKey.Factory::class.java).to(BridgerKey.Factory::class.java)

    bind(AnkhCoreLoader::class.java).toProvider {
      (AnkhCoreLoaderPlugin.container as AnkhPluginContainerImpl).bukkitPlugin as AnkhCoreLoader
    }

    bind(AnkhPluginManager::class.java).to(AnkhPluginManagerImpl::class.java)
    bind(AnkhPluginManagerImpl::class.java).toInstance(AnkhPluginManagerImpl)

    bind(StorageBackend::class.java).annotatedWith(Names.named("database")).to(DatabaseBackend::class.java)
    bind(StorageBackend::class.java).annotatedWith(Names.named("filesystem")).to(FilesystemBackend::class.java)

    bind(HologramService::class.java).toProvider(HologramProvider::class.java)

    bind(WorldService::class.java).to(AnkhWorldService::class.java)

    bind(BlockRegistry::class.java).to(BlockRegisterService::class.java)
    bind(AnkhItemRegistry::class.java).to(ItemRegisterService::class.java)
  }
}