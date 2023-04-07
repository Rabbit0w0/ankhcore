package org.inksnow.ankh.core.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.ankh.core.api.AnkhCore;
import org.inksnow.ankh.core.api.block.AnkhBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Slf4j
public class ProtectDataBlock implements AnkhBlock {
  private static final Component PROTECT_MESSAGE = Component.text()
      .append(AnkhCore.PLUGIN_NAME_COMPONENT)
      .append(Component.text(" Sorry, but this block isn't load correctly"))
      .append(Component.newline())
      .append(Component.text("It may cause by some extensions broke"))
      .build();
  private final Key key;
  private final byte[] data;

  public ProtectDataBlock(Key key, byte[] data) {
    this.key = key;
    this.data = data;
  }

  private static void sendProtectMessage(@Nullable Player player) {
    if (player == null) {
      return;
    }
    player.sendMessage(PROTECT_MESSAGE);
  }

  @Override
  public void load(@Nonnull Location location) {
    location.getWorld().getBlockAt(location).setType(Material.BEDROCK);
  }

  @Override
  public void unload() {
    //
  }

  @Override
  public void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
    sendProtectMessage(event.getPlayer());
    event.setCancelled(true);
  }

  @Override
  public void onBlockBreak(@Nonnull BlockBreakEvent event) {
    sendProtectMessage(event.getPlayer());
    event.setCancelled(true);
  }

  @Override
  public void onBlockDestroy(@Nonnull BlockDestroyEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void remove(boolean isDestroy) {
    logger.error("protect data have been removed", new RuntimeException("protect data have been removed"));
  }

  @Override
  public @NonNull Key key() {
    return key;
  }

  @Override
  public byte[] save() {
    return data;
  }
}
