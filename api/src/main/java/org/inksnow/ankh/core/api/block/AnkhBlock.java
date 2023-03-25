package org.inksnow.ankh.core.api.block;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.inksnow.ankh.core.api.AnkhCore;
import org.inksnow.ankh.core.api.world.WorldService;

import javax.annotation.Nonnull;

/**
 * AnkhBlock, which can contains data and storage in WorldService
 * AnkhBlock can be tickable, async-tickable
 * <p>
 * DON'T SAVE HARD REF TO BLOCK INSTANCE IN ANKH_BLOCK, IT WILL CAUSE MEMORY LEAK
 *
 * @see WorldService
 */
public interface AnkhBlock extends Keyed {
  /**
   * short link to factory, work as <code>AnkhCore.getInstance(Factory.class);</code>
   *
   * @return AnkhBlock.Factory
   */
  static @Nonnull Factory factory() {
    return AnkhCore.getInstance(Factory.class);
  }

  /**
   * Called when block placed in location (or loaded in location by WorldService)
   *
   * @param location the location the block in
   */
  void load(@Nonnull Location location);

  /**
   * Unload block, when chunk have been unloaded
   * Don't use chunk api in location, it may cause serious problems
   */
  void unload();

  /**
   * Called when block removed in world (break by player or more)
   *
   * @param isDestroy is the block being destroy
   */
  void remove(boolean isDestroy);

  /**
   * Save block data, will store in WorldService, and will load in Factory.load
   *
   * @return bytes need storage
   * @see Factory#load(Key, byte[])
   */
  default byte[] save() {
    return new byte[0];
  }

  /**
   * Called on BlockBreakEvent
   *
   * @param event the event
   * @see BlockBreakEvent
   */
  default void onBlockBreak(@Nonnull BlockBreakEvent event) {
    //
  }

  /**
   * Called on BlockRedstoneEvent
   *
   * @param event the event
   * @see BlockRedstoneEvent
   */
  default void onBlockRedstone(@Nonnull BlockRedstoneEvent event) {
    //
  }

  /**
   * Called on PlayerInteractEvent
   *
   * @param event the event
   * @see PlayerInteractEvent
   */
  default void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
    //
  }

  /**
   * AnkhBlock Factory, load from data by WorldService
   *
   * @see WorldService
   */
  interface Factory extends Keyed {
    /**
     * load AnkhBlock from data stored in <code>AnkhBlock.save()</code>
     *
     * @param id   the block id
     * @param data the data stored before
     * @return the block instance
     * @see AnkhBlock#save()
     */
    @Nonnull
    AnkhBlock load(@Nonnull Key id, @Nonnull byte[] data);
  }
}
