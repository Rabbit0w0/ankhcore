package bot.inker.ankh.core.world

import bot.inker.ankh.core.api.AnkhCoreLoader
import bot.inker.ankh.core.api.block.AnkhBlock
import bot.inker.ankh.core.api.block.AsyncTickableBlock
import bot.inker.ankh.core.api.block.TickableBlock
import bot.inker.ankh.core.api.plugin.PluginLifeCycle
import bot.inker.ankh.core.api.plugin.annotations.SubscriptEvent
import bot.inker.ankh.core.api.plugin.annotations.SubscriptLifecycle
import bot.inker.ankh.core.api.world.WorldService
import bot.inker.ankh.core.api.world.storage.BlockStorageEntry
import bot.inker.ankh.core.api.world.storage.StorageBackend
import bot.inker.ankh.core.block.BlockRegisterService
import bot.inker.ankh.core.common.config.AnkhConfig
import bot.inker.ankh.core.common.dsl.getValue
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.common.dsl.setValue
import bot.inker.ankh.core.common.entity.LocationEmbedded
import bot.inker.ankh.core.common.entity.WorldChunkEmbedded
import bot.inker.ankh.core.database.DatabaseService
import com.destroystokyo.paper.event.block.BlockDestroyEvent
import com.google.common.collect.MapMaker
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.name.Names
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventPriority
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.*
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnkhWorldService @Inject private constructor(
  private val injector: Injector,
  private val loaderPlugin: AnkhCoreLoader,
  private val ankhConfig: AnkhConfig,
  private val blockRegisterService: BlockRegisterService,
  private val databaseService: DatabaseService,
) : WorldService {
  private val logger by logger()
  private val worldMap = MapMaker().makeMap<UUID, WorldStorage>()
  private val storageBackend = injector.getInstance(
    Key.get(
      StorageBackend::class.java,
      Names.named(ankhConfig.worldStorage.backend)
    )
  )

  override fun getBlock(location: Location): AnkhBlock? {
    val locationEmbedded = LocationEmbedded.of(location)
    val chunkStorage = getWorldStorage(locationEmbedded.chunk().worldId()).getChunkStorage(locationEmbedded.chunk())
    if (!chunkStorage.loaded || chunkStorage.loadFailure) {
      return null
    }
    return chunkStorage.blockMap[locationEmbedded]
  }

  override fun setBlock(location: Location, ankhBlock: AnkhBlock) {
    val locationEmbedded = LocationEmbedded.of(location)
    val chunkStorage = getWorldStorage(locationEmbedded.chunk().worldId())
      .getChunkStorage(locationEmbedded.chunk())
    handleBlockSet(chunkStorage, locationEmbedded, location, ankhBlock)
  }

  override fun removeBlock(location: Location) {
    val locationEmbedded = LocationEmbedded.of(location)
    val chunkStorage = getWorldStorage(locationEmbedded.chunk().worldId())
      .getChunkStorage(locationEmbedded.chunk())
    val ankhBlock = chunkStorage.blockMap[locationEmbedded]
    if (ankhBlock == null) {
      logger.warn("Try to remove no-exist ankh-block at {}", location)
      return
    }
    handleBlockRemove(chunkStorage, locationEmbedded, ankhBlock)
  }

  fun getBlockImpl(location: Location): Pair<Boolean, AnkhBlock?> {
    val locationEmbedded = LocationEmbedded.of(location)
    val chunkStorage = getWorldStorage(locationEmbedded.chunk().worldId()).getChunkStorage(locationEmbedded.chunk())
    if (!chunkStorage.loaded || chunkStorage.loadFailure) {
      return false to null
    }
    return true to chunkStorage.blockMap[locationEmbedded]
  }

  private fun loadChunk(world: World, worldChunk: WorldChunkEmbedded) {
    val worldStorage = getWorldStorage(worldChunk.worldId())
    val chunkStorage = worldStorage.chunks.computeIfAbsent(worldChunk, ::ChunkStorage)

    Bukkit.getScheduler().runTaskAsynchronously(loaderPlugin, Runnable {
      val asyncStartTime = System.nanoTime()
      val syncActions = ArrayList<() -> Unit>()
      try {
        storageBackend.provide(worldChunk).forEach { storedBlock ->
          blockRegisterService.get(storedBlock.blockId())
            ?.load(storedBlock.blockId(), storedBlock.content())
            ?.let { ankhBlock ->
              syncActions.add {
                handleBlockSet(
                  chunkStorage = chunkStorage,
                  locationEmbedded = LocationEmbedded.warp(storedBlock.location()),
                  location = Location(
                    world,
                    storedBlock.location().x().toDouble(),
                    storedBlock.location().y().toDouble(),
                    storedBlock.location().z().toDouble()
                  ),
                  ankhBlock = ankhBlock
                )
              }
            }
        }

        val asyncPassTime = System.nanoTime() - asyncStartTime
        Bukkit.getScheduler().runTask(loaderPlugin, Runnable {
          val startTime = System.nanoTime()
          syncActions.forEach {
            it()
          }
          chunkStorage.loaded = true
          val passTime = System.nanoTime() - startTime
          logger.trace(
            "load world:{} x:{} z:{} asyncTime:{} syncTime:{}",
            world.name,
            worldChunk.x(),
            worldChunk.z(),
            asyncPassTime,
            passTime
          )
        })
      } catch (e: Exception) {
        chunkStorage.loadFailure = true
        logger.warn("Failed to load chunk:{} x:{} z:{}", world.name, worldChunk.x(), worldChunk.z(), e)
      }

    })
  }

  private fun saveChunk(worldChunk: WorldChunkEmbedded, unload: Boolean = false) {
    val worldStorage = getWorldStorage(worldChunk.worldId())
    worldStorage.chunks[worldChunk]?.let { chunkStorage ->
      if (!chunkStorage.loaded || chunkStorage.loadFailure) {
        return
      }
      val startTime = System.nanoTime()
      val entries = chunkStorage.blockMap
        .map { (location, block) ->
          BlockStorageEntry.of(
            location,
            block.key(),
            block.save()
          )
        }
      if (unload) {
        chunkStorage.blockMap.forEach { it.value.unload() }
        worldStorage.chunks.remove(worldChunk)
      }
      val passTime = System.nanoTime() - startTime
      if (entries.isNotEmpty()) {
        val task = Runnable {
          val asyncStartTime = System.nanoTime()
          try {
            storageBackend.store(worldChunk, entries)
            chunkStorage.loaded = true
          } catch (e: Exception) {
            chunkStorage.loadFailure = true
            logger.warn("Failed to save chunk:{} x:{} z:{}", worldChunk.worldId(), worldChunk.x(), worldChunk.z(), e)
          }
          val asyncPassTime = System.nanoTime() - asyncStartTime
          logger.trace(
            "unload world:{} x:{} z:{} syncTime:{} asyncTime:{}",
            worldChunk.worldId(),
            worldChunk.x(),
            worldChunk.z(),
            passTime,
            asyncPassTime
          )
        }
        if (loaderPlugin.isEnabled) {
          Bukkit.getScheduler().runTaskAsynchronously(loaderPlugin, task)
        } else {
          task.run()
        }
      } else {
        logger.trace(
          "unload world:{} x:{} z:{} syncTime:{}",
          worldChunk.worldId(),
          worldChunk.x(),
          worldChunk.z(),
          passTime
        )
      }
    }
  }

  private fun getWorldStorage(worldUUID: UUID): WorldStorage {
    return worldMap.computeIfAbsent(worldUUID, ::WorldStorage)
  }

  private fun handleBlockRemove(
    chunkStorage: ChunkStorage,
    locationEmbedded: LocationEmbedded,
    ankhBlock: AnkhBlock,
  ) {
    ankhBlock.remove(true)
    ankhBlock.unload()
    chunkStorage.blockMap.remove(locationEmbedded)
    if (ankhBlock is TickableBlock) {
      chunkStorage.blockTickers.remove(locationEmbedded)
    }
    if (ankhBlock is AsyncTickableBlock) {
      chunkStorage.asyncTickers.remove(locationEmbedded)
    }
  }

  private fun handleBlockSet(
    chunkStorage: ChunkStorage,
    locationEmbedded: LocationEmbedded,
    location: Location,
    ankhBlock: AnkhBlock,
  ) {
    chunkStorage.blockMap[locationEmbedded] = ankhBlock
    if (ankhBlock is TickableBlock) {
      chunkStorage.blockTickers[locationEmbedded] = ankhBlock
    }
    if (ankhBlock is AsyncTickableBlock) {
      chunkStorage.asyncTickers[locationEmbedded] = ankhBlock
    }
    ankhBlock.load(location)
  }

  private fun runTick(worldStorage: WorldStorage) {
    worldStorage.chunks.values.forEach(this::runTick)
  }

  private fun runTick(chunkStorage: ChunkStorage) {
    chunkStorage.blockTickers.forEach(this::runTick)
  }

  private fun runTick(location: LocationEmbedded, block: TickableBlock) {
    try {
      block.runTick(location)
    } catch (e: InterruptedException) {
      throw e
    } catch (e: Exception) {
      logger.warn("Failed to run sync tick for block {}", location, e)
    }
  }

  private fun runAsyncTick(worldStorage: WorldStorage) {
    worldStorage.chunks.values.forEach(this::runAsyncTick)
  }

  private fun runAsyncTick(chunkStorage: ChunkStorage) {
    chunkStorage.asyncTickers.forEach(this::runAsyncTick)
  }

  private fun runAsyncTick(location: LocationEmbedded, block: AsyncTickableBlock) {
    try {
      block.runAsyncTick(location)
    } catch (e: InterruptedException) {
      throw e
    } catch (e: Exception) {
      logger.warn("Failed to run async tick for block {}", location, e)
    }
  }

  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private fun registerTickService() {
    object : BukkitRunnable() {
      override fun run() {
        try {
          worldMap.values.forEach(this@AnkhWorldService::runTick)
        } catch (e: InterruptedException) {
          throw e
        } catch (e: Exception) {
          logger.warn("Failed to run sync tick", e)
        }
      }
    }.runTaskTimer(loaderPlugin, 0, ankhConfig.tickRate.toLong())

    object : BukkitRunnable() {
      override fun run() {
        try {
          worldMap.values.forEach(this@AnkhWorldService::runAsyncTick)
        } catch (e: InterruptedException) {
          throw e
        } catch (e: Exception) {
          logger.warn("Failed to run async tick", e)
        }
      }
    }.runTaskTimerAsynchronously(loaderPlugin, 0, ankhConfig.tickRate.toLong())
  }

  @SubscriptEvent
  private fun onWorldLoad(event: WorldLoadEvent) {
    worldMap.computeIfAbsent(event.world.uid, ::WorldStorage)
  }

  @SubscriptEvent
  private fun onWorldUnload(event: WorldUnloadEvent) {
    worldMap.remove(event.world.uid)
  }

  @SubscriptEvent
  private fun onWorldSave(event: WorldSaveEvent) {
    worldMap[event.world.uid]?.let { worldStorage ->
      worldStorage.chunks.keys.forEach { worldChunk ->
        saveChunk(worldChunk)
      }
    }
  }

  @SubscriptEvent
  private fun onChunkLoad(event: ChunkLoadEvent) {
    loadChunk(event.world, WorldChunkEmbedded.of(event.chunk))
  }

  @SubscriptEvent
  private fun onChunkUnload(event: ChunkUnloadEvent) {
    saveChunk(WorldChunkEmbedded.of(event.chunk), true)
  }

  @SubscriptEvent(ignoreCancelled = true, priority = EventPriority.LOWEST)
  private fun onBlockPistonRetract(event: BlockPistonRetractEvent) {
    if (event.blocks.any {
        val blockStatus = getBlockImpl(it.location)
        !blockStatus.first || blockStatus.second != null
      }) {
      event.isCancelled = true
    }
  }

  @SubscriptEvent(ignoreCancelled = true, priority = EventPriority.LOWEST)
  private fun onBlockPistonExtend(event: BlockPistonExtendEvent) {
    if (event.blocks.any {
        val blockStatus = getBlockImpl(it.location)
        !blockStatus.first || blockStatus.second != null
      }) {
      event.isCancelled = true
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private fun onBlockExplode(event: BlockExplodeEvent) {
    event.blockList().removeAll {
      val blockStatus = getBlockImpl(it.location)
      !blockStatus.first || blockStatus.second != null
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private fun onBlockExplode(event: EntityExplodeEvent) {
    event.blockList().removeAll {
      val blockStatus = getBlockImpl(it.location)
      !blockStatus.first || blockStatus.second != null
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private fun onBlockExplode(event: EntityChangeBlockEvent) {
    val blockStatus = getBlockImpl(event.block.location)
    if (!blockStatus.first || blockStatus.second != null) {
      event.isCancelled = true
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private fun onBlockBreakLowest(event: BlockBreakEvent) {
    val blockStatus = getBlockImpl(event.block.location)
    if (!blockStatus.first) {
      event.isCancelled = true
    } else if (blockStatus.second != null) {
      event.isDropItems = false
      event.expToDrop = 0
    }
  }

  @SubscriptEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private fun onBlockBreakMonitor(event: BlockBreakEvent) {
    val locationEmbedded = LocationEmbedded.of(event.block.location)
    val chunkStorage = getWorldStorage(locationEmbedded.chunk().worldId()).getChunkStorage(locationEmbedded.chunk())
    val ankhBlock = chunkStorage.blockMap[locationEmbedded] ?: return
    handleBlockRemove(chunkStorage, locationEmbedded, ankhBlock)
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private fun onBlockDestroyLowest(event: BlockDestroyEvent) {
    val blockStatus = getBlockImpl(event.block.location)
    if (!blockStatus.first) {
      event.isCancelled = true
    }
  }

  @SubscriptEvent(priority = EventPriority.MONITOR)
  private fun onBlockDestroyMonitor(event: BlockDestroyEvent) {
    val locationEmbedded = LocationEmbedded.of(event.block.location)
    val chunkStorage = getWorldStorage(locationEmbedded.chunk().worldId()).getChunkStorage(locationEmbedded.chunk())
    val ankhBlock = chunkStorage.blockMap[locationEmbedded] ?: return
    handleBlockRemove(chunkStorage, locationEmbedded, ankhBlock)
  }

  @SubscriptLifecycle(PluginLifeCycle.DISABLE)
  private fun onPluginDisable() {
    logger.info("Plugin disable, saving world storage")
    worldMap.forEach { (_, worldStorage) ->
      worldStorage.chunks.keys.forEach { worldChunk ->
        saveChunk(worldChunk, true)
      }
    }
  }

  // listeners for block start

  @SubscriptEvent
  private fun onBlockRedstone(event: BlockRedstoneEvent) {
    val ankhBlock = getBlock(event.block.location) ?: return
    ankhBlock.onBlockRedstone(event)
  }

  @SubscriptEvent
  private fun onBlockBreak(event: BlockBreakEvent) {
    val ankhBlock = getBlock(event.block.location) ?: return
    ankhBlock.onBlockBreak(event)
  }

  @SubscriptEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private fun onPlayerInteract(event: PlayerInteractEvent) {
    val block = event.clickedBlock ?: return
    if (block.type == Material.AIR) {
      return
    }
    val ankhBlock = getBlock(block.location) ?: return
    ankhBlock.onPlayerInteract(event)
  }

  class WorldStorage(
    val worldId: UUID,
  ) {
    val chunks = MapMaker().makeMap<WorldChunkEmbedded, ChunkStorage>()
    fun getChunkStorage(worldChunk: WorldChunkEmbedded): ChunkStorage {
      return chunks.computeIfAbsent(worldChunk, ::ChunkStorage)
    }
  }

  class ChunkStorage(
    val worldChunk: WorldChunkEmbedded,
  ) {
    var loaded by AtomicBoolean(false)
    var loadFailure by AtomicBoolean(false)

    val blockTickers = MapMaker().makeMap<LocationEmbedded, TickableBlock>()
    val asyncTickers = MapMaker().makeMap<LocationEmbedded, AsyncTickableBlock>()
    val blockMap = MapMaker().makeMap<LocationEmbedded, AnkhBlock>()
  }
}