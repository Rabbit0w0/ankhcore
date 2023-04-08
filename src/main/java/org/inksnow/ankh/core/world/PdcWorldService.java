package org.inksnow.ankh.core.world;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.*;
import org.inksnow.ankh.core.api.AnkhCoreLoader;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.block.AnkhBlock;
import org.inksnow.ankh.core.api.block.AsyncTickableBlock;
import org.inksnow.ankh.core.api.block.BlockRegistry;
import org.inksnow.ankh.core.api.block.TickableBlock;
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle;
import org.inksnow.ankh.core.api.world.WorldService;
import org.inksnow.ankh.core.api.world.storage.BlockStorageEntry;
import org.inksnow.ankh.core.block.ProtectDataBlock;
import org.inksnow.ankh.core.common.config.AnkhConfig;
import org.inksnow.ankh.core.common.entity.LocationEmbedded;
import org.inksnow.ankh.core.common.entity.WorldChunkEmbedded;
import org.inksnow.ankh.core.common.util.FastEmbeddedUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

@Singleton
@Slf4j
@SuppressWarnings("DuplicatedCode") // for fast
public class PdcWorldService implements WorldService {
  private static final org.inksnow.ankh.core.api.world.storage.WorldStorage storageBackend =
      AnkhServiceLoader.service(org.inksnow.ankh.core.api.world.storage.WorldStorage.class);
  private final AnkhCoreLoader coreLoader;
  private final AnkhConfig ankhConfig;
  private final BlockRegistry blockRegistry;
  private final Map<UUID, WorldStorage> worldMap = new Object2ObjectAVLTreeMap<>();
  private final Predicate<Block> isUnloadOrAnkh = this::isUnloadOrAnkh;

  @Inject
  private PdcWorldService(AnkhCoreLoader coreLoader, AnkhConfig ankhConfig, BlockRegistry blockRegistry) {
    this.coreLoader = coreLoader;
    this.ankhConfig = ankhConfig;
    this.blockRegistry = blockRegistry;
  }

  @Override
  public @Nullable AnkhBlock getBlock(@Nonnull Location location) {
    val x = location.getBlockX();
    val y = location.getBlockY();
    val z = location.getBlockZ();

    val worldStorage = worldMap.get(location.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("try to get block at unloaded world, location={}", location);
      return null;
    }

    val chunkStorage = worldStorage.chunks.get(FastEmbeddedUtil.location_chunkId(x, z));
    if (chunkStorage == null || !chunkStorage.loaded.get() || chunkStorage.loadFailure.get()) {
      logger.warn("try to get block at unloaded chunk, location={}", location);
      return null;
    }

    return chunkStorage.blockMap.get(FastEmbeddedUtil.blockId(x, y, z));
  }

  public Pair<Boolean, AnkhBlock> getBlockImpl(@Nonnull Location location) {
    val x = location.getBlockX();
    val y = location.getBlockY();
    val z = location.getBlockZ();

    val worldStorage = worldMap.get(location.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("try to get block at unloaded world, location={}", location);
      return Pair.of(false, null);
    }

    val chunkStorage = worldStorage.chunks.get(FastEmbeddedUtil.location_chunkId(x, z));
    if (chunkStorage == null || !chunkStorage.loaded.get() || chunkStorage.loadFailure.get()) {
      logger.warn("try to get block at unloaded chunk, location={}", location);
      return Pair.of(false, null);
    }

    return Pair.of(true, chunkStorage.blockMap.get(FastEmbeddedUtil.blockId(x, y, z)));
  }

  @Override
  public void setBlock(@Nonnull Location location, @Nonnull AnkhBlock ankhBlock) {
    val x = location.getBlockX();
    val y = location.getBlockY();
    val z = location.getBlockZ();

    val worldStorage = worldMap.get(location.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("try to set block at unloaded world, location={}", location);
      return;
    }

    val chunkStorage = worldStorage.chunks.get(FastEmbeddedUtil.location_chunkId(x, z));
    if (chunkStorage == null || !chunkStorage.loaded.get() || chunkStorage.loadFailure.get()) {
      logger.warn("try to set block at unloaded chunk, location={}", location);
      return;
    }

    handleBlockSet(chunkStorage, location, FastEmbeddedUtil.blockId(x, y, z), ankhBlock);
  }

  @Override
  public void removeBlock(@Nonnull Location location) {
    val x = location.getBlockX();
    val y = location.getBlockY();
    val z = location.getBlockZ();

    val worldStorage = worldMap.get(location.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("try to set block at unloaded world, location={}", location);
      return;
    }

    val chunkStorage = worldStorage.chunks.get(FastEmbeddedUtil.location_chunkId(x, z));
    if (chunkStorage == null || !chunkStorage.loaded.get() || chunkStorage.loadFailure.get()) {
      logger.warn("try to set block at unloaded chunk, location={}", location);
      return;
    }

    val blockId = FastEmbeddedUtil.blockId(x, y, z);
    val ankhBlock = chunkStorage.blockMap.get(blockId);
    if (ankhBlock == null) {
      logger.warn("Try to remove no-exist ankh-block at {}", location);
      return;
    }
    handleBlockRemove(chunkStorage, blockId, ankhBlock);
  }

  private void handleBlockSet(ChunkStorage chunkStorage, Location location, long blockId, AnkhBlock ankhBlock) {
    chunkStorage.blockMap.put(blockId, ankhBlock);
    if (ankhBlock instanceof TickableBlock) {
      chunkStorage.blockTickers.put(blockId, (TickableBlock) ankhBlock);
    }
    if (ankhBlock instanceof AsyncTickableBlock) {
      chunkStorage.asyncTickers.put(blockId, (AsyncTickableBlock) ankhBlock);
    }
    ankhBlock.load(location);
  }

  private void handleBlockRemove(ChunkStorage chunkStorage, long blockId, AnkhBlock ankhBlock) {
    ankhBlock.remove(true);
    ankhBlock.unload();
    chunkStorage.blockMap.remove(blockId);
    if (ankhBlock instanceof TickableBlock) {
      chunkStorage.blockTickers.remove(blockId);
    }
    if (ankhBlock instanceof AsyncTickableBlock) {
      chunkStorage.asyncTickers.remove(blockId);
    }
  }

  private void saveChunk(ChunkStorage chunkStorage, Chunk chunk) {
    val chunkEmbedded = WorldChunkEmbedded.of(chunk);

    val entrySet = chunkStorage.blockMap.long2ObjectEntrySet();
    val entryList = new ArrayList<BlockStorageEntry>(entrySet.size());
    for (val entry : entrySet) {
      val locationEmbedded = LocationEmbedded.of(chunkEmbedded, entry.getLongKey());
      entryList.add(BlockStorageEntry.of(
          locationEmbedded,
          entry.getValue().key(),
          entry.getValue().save()
      ));
    }

    val asyncTask = (Runnable) () -> {
      storageBackend.store(chunk, entryList);
    };

    if (coreLoader.isEnabled()) {
      Bukkit.getScheduler().runTaskAsynchronously(coreLoader, asyncTask);
    } else {
      asyncTask.run();
    }
  }

  private void loadChunk(ChunkStorage chunkStorage, Chunk chunk) {
    Bukkit.getScheduler().runTaskAsynchronously(coreLoader, () -> {
      try {
        val entryList = storageBackend.provide(chunk);

        Bukkit.getScheduler().runTask(coreLoader, () -> {
          try {
            for (val entry : entryList) {
              handleBlockSet(
                  chunkStorage,
                  new Location(chunk.getWorld(), entry.location().x(), entry.location().y(), entry.location().z()),
                  LocationEmbedded.warp(entry.location()).position(),
                  loadBlock(entry.blockId(), entry.content())
              );
            }
            chunkStorage.loaded.set(true);
          } catch (Exception e) {
            chunkStorage.loadFailure.set(true);
            logger.error("Failed to load chunk storage", e);
          }
        });
      } catch (Exception e) {
        chunkStorage.loadFailure.set(true);
        logger.error("Failed to load chunk storage", e);
      }
    });
  }

  private AnkhBlock loadBlock(Key blockId, byte[] content) {
    val blockFactory = blockRegistry.get(blockId);
    if (blockFactory == null) {
      return new ProtectDataBlock(blockId, content);
    }
    try {
      return blockFactory.load(blockId, content);
    } catch (Exception e) {
      logger.warn("Failed to load block {}", blockId, e);
      return new ProtectDataBlock(blockId, content);
    }
  }

  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private void onPluginEnable() {
    Bukkit.getScheduler().runTaskTimer(coreLoader, () -> {
      for (val worldStorageEntry : worldMap.entrySet()) {
        for (val chunkStorageEntry : worldStorageEntry.getValue().chunks.long2ObjectEntrySet()) {
          for (val blockEntry : chunkStorageEntry.getValue().blockTickers.long2ObjectEntrySet()) {
            try {
              blockEntry.getValue().runTick();
            } catch (Exception e) {
              logger.error(
                  "Failed to run tick for block chunk(x={}, z={}), location(x={}, y={}, z={})",
                  FastEmbeddedUtil.chunkX(chunkStorageEntry.getLongKey()),
                  FastEmbeddedUtil.chunkZ(chunkStorageEntry.getLongKey()),
                  FastEmbeddedUtil.chunk_blockId_x(chunkStorageEntry.getLongKey(), blockEntry.getLongKey()),
                  FastEmbeddedUtil.blockId_y(blockEntry.getLongKey()),
                  FastEmbeddedUtil.chunk_blockId_z(chunkStorageEntry.getLongKey(), blockEntry.getLongKey()),
                  e
              );
            }
          }
        }
      }
    }, 0, ankhConfig.tickRate());
    Bukkit.getScheduler().runTaskTimerAsynchronously(coreLoader, () -> {
      for (val worldStorageEntry : worldMap.entrySet()) {
        for (val chunkStorageEntry : worldStorageEntry.getValue().chunks.long2ObjectEntrySet()) {
          for (val blockEntry : chunkStorageEntry.getValue().asyncTickers.long2ObjectEntrySet()) {
            try {
              blockEntry.getValue().runAsyncTick();
            } catch (Exception e) {
              logger.error(
                  "Failed to run async tick for block chunk(x={}, z={}), location(x={}, y={}, z={})",
                  FastEmbeddedUtil.chunkX(chunkStorageEntry.getLongKey()),
                  FastEmbeddedUtil.chunkZ(chunkStorageEntry.getLongKey()),
                  FastEmbeddedUtil.chunk_blockId_x(chunkStorageEntry.getLongKey(), blockEntry.getLongKey()),
                  FastEmbeddedUtil.blockId_y(blockEntry.getLongKey()),
                  FastEmbeddedUtil.chunk_blockId_z(chunkStorageEntry.getLongKey(), blockEntry.getLongKey()),
                  e
              );
            }
          }
        }
      }
    }, 0, ankhConfig.tickRate());
  }

  @SubscriptEvent
  private void onWorldLoad(WorldLoadEvent event) {
    worldMap.computeIfAbsent(event.getWorld().getUID(), PdcWorldService.WorldStorage.create);
  }

  @SubscriptEvent
  private void onWorldUnload(WorldUnloadEvent event) {
    worldMap.remove(event.getWorld().getUID());
  }

  @SubscriptEvent
  private void onWorldSave(WorldSaveEvent event) {
    val worldStorage = worldMap.get(event.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("call WorldSaveEvent at unloaded world");
      return;
    }
    for (val loadedChunk : event.getWorld().getLoadedChunks()) {
      val chunkStorage = worldStorage.chunks.get(
          FastEmbeddedUtil.chunk_chunkId(loadedChunk.getX(), loadedChunk.getZ())
      );
      if (chunkStorage == null) {
        continue;
      }
      saveChunk(chunkStorage, loadedChunk);
    }
  }

  @SubscriptEvent
  private void onChunkLoad(ChunkLoadEvent event) {
    val worldStorage = worldMap.get(event.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("call ChunkLoadEvent at unloaded world");
      return;
    }
    val chunkId = FastEmbeddedUtil.chunk_chunkId(event.getChunk().getX(), event.getChunk().getZ());
    val chunkStorage = worldStorage.chunks.computeIfAbsent(chunkId, ChunkStorage.create);
    try {
      loadChunk(chunkStorage, event.getChunk());
      chunkStorage.loaded.set(true);
    } catch (Exception e) {
      logger.warn("Failed to load chunk", e);
      chunkStorage.loadFailure.set(true);
    }
  }

  @SubscriptEvent
  private void onChunkUnload(ChunkUnloadEvent event) {
    val worldStorage = worldMap.get(event.getWorld().getUID());
    if (worldStorage == null) {
      logger.warn("call ChunkUnloadEvent at unloaded world");
      return;
    }
    val chunkId = FastEmbeddedUtil.chunk_chunkId(event.getChunk().getX(), event.getChunk().getZ());
    val chunkStorage = worldStorage.chunks.get(chunkId);
    if (chunkStorage == null) {
      logger.warn("call ChunkUnloadEvent at unloaded chunk");
      return;
    }
    saveChunk(chunkStorage, event.getChunk());
  }

  // = block listener start ============================================================================================

  @SubscriptLifecycle(PluginLifeCycle.DISABLE)
  private void onPluginDisable() {
    logger.info("Plugin disable, saving world storage");
    for (val worldStorageEntry : worldMap.entrySet()) {
      val bukkitWorld = Bukkit.getWorld(worldStorageEntry.getKey());
      if (bukkitWorld == null) {
        logger.warn("try to save block at un-exist world");
        continue;
      }
      for (val chunk : bukkitWorld.getLoadedChunks()) {
        val chunkId = FastEmbeddedUtil.chunk_chunkId(chunk.getX(), chunk.getZ());
        val chunkStorage = worldStorageEntry.getValue().chunks.get(chunkId);
        if (chunkStorage == null) {
          logger.warn("try to save chunk at unload chunk");
          continue;
        }
        saveChunk(chunkStorage, chunk);
      }
    }
  }

  private boolean isUnloadOrAnkh(Block block) {
    val blockStatus = getBlockImpl(block.getLocation());
    return !blockStatus.first() || blockStatus.second() != null;
  }

  @SubscriptEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private void onBlockPistonRetract(BlockPistonRetractEvent event) {
    for (val block : event.getBlocks()) {
      if (isUnloadOrAnkh(block)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private void onBlockPistonExtend(BlockPistonExtendEvent event) {
    for (val block : event.getBlocks()) {
      if (isUnloadOrAnkh(block)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private void onBlockExplode(BlockExplodeEvent event) {
    event.blockList().removeIf(isUnloadOrAnkh);
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private void onBlockExplode(EntityExplodeEvent event) {
    event.blockList().removeIf(isUnloadOrAnkh);
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private void onBlockExplode(EntityChangeBlockEvent event) {
    if (isUnloadOrAnkh(event.getBlock())) {
      event.setCancelled(true);
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST)
  private void onBlockBreakLowest(BlockBreakEvent event) {
    val blockStatus = getBlockImpl(event.getBlock().getLocation());
    if (!blockStatus.first()) {
      event.setCancelled(true);
    } else if (blockStatus.second() != null) {
      event.setDropItems(false);
      event.setExpToDrop(0);
    }
  }

  @SubscriptEvent(ignoreCancelled = true)
  private void onBlockBreak(BlockBreakEvent event) {
    val ankhBlock = getBlock(event.getBlock().getLocation());
    if (ankhBlock != null) {
      ankhBlock.onBlockBreak(event);
    }
  }

  @SubscriptEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private void onBlockBreakMonitor(BlockBreakEvent event) {
    val location = event.getBlock().getLocation();
    val ankhBlock = getBlock(location);
    if (ankhBlock != null) {
      removeBlock(location);
    }
  }

  @SubscriptEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private void onBlockDestroyLowest(BlockDestroyEvent event) {
    val blockStatus = getBlockImpl(event.getBlock().getLocation());
    if (!blockStatus.first()) {
      event.setCancelled(true);
    }
  }

  @SubscriptEvent(ignoreCancelled = true)
  private void onBlockDestroy(BlockDestroyEvent event) {
    val ankhBlock = getBlock(event.getBlock().getLocation());
    if (ankhBlock != null) {
      ankhBlock.onBlockDestroy(event);
    }
  }

  @SubscriptEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private void onBlockDestroyMonitor(BlockDestroyEvent event) {
    val location = event.getBlock().getLocation();
    val ankhBlock = getBlock(location);
    if (ankhBlock != null) {
      removeBlock(location);
    }
  }

  @SubscriptEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private void onBlockRedstone(BlockRedstoneEvent event) {
    val ankhBlock = getBlock(event.getBlock().getLocation());
    if (ankhBlock != null) {
      ankhBlock.onBlockRedstone(event);
    }
  }

  @SubscriptEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private void onPlayerInteract(PlayerInteractEvent event) {
    if ((event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK)
        || event.useInteractedBlock() == Event.Result.DENY
        || event.getPlayer().isSneaking()) {
      return;
    }
    val block = event.getClickedBlock();
    if (block == null || block.getType() == Material.AIR) {
      return;
    }
    val ankhBlock = getBlock(block.getLocation());
    if (ankhBlock != null) {
      ankhBlock.onPlayerInteract(event);
    }
  }

  private static class WorldStorage {
    private static final Function<Object, WorldStorage> create = it -> new WorldStorage();

    private final Long2ObjectMap<ChunkStorage> chunks = new Long2ObjectRBTreeMap<>();
  }

  private static class ChunkStorage {
    private static final Function<Object, ChunkStorage> create = it -> new ChunkStorage();

    private final AtomicBoolean loaded = new AtomicBoolean(false);
    private final AtomicBoolean loadFailure = new AtomicBoolean(false);
    private final Long2ObjectMap<TickableBlock> blockTickers = new Long2ObjectRBTreeMap<>();
    private final Long2ObjectMap<AsyncTickableBlock> asyncTickers = new Long2ObjectRBTreeMap<>();
    private final Long2ObjectMap<AnkhBlock> blockMap = new Long2ObjectRBTreeMap<>();
  }
}
