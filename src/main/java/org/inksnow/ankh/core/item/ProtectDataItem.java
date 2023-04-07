package org.inksnow.ankh.core.item;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhCore;
import org.inksnow.ankh.core.api.item.AnkhItem;

import javax.annotation.Nonnull;

public class ProtectDataItem implements AnkhItem {
  private static final Component PROTECT_MESSAGE = Component.text()
      .append(AnkhCore.PLUGIN_NAME_COMPONENT)
      .append(Component.text(" Sorry, but this item isn't load correctly"))
      .append(Component.newline())
      .append(Component.text("It may cause by some extensions broke"))
      .build();

  private static final Key PROTECT_ITEM_KEY = Key.key(AnkhCore.PLUGIN_ID, "protect-item");

  @Getter
  private static final ProtectDataItem instance = new ProtectDataItem();

  private ProtectDataItem() {
    //
  }

  private static void sendProtectMessage(Player player) {
    if (player != null) {
      player.sendMessage(PROTECT_MESSAGE);
    }
  }

  @Override
  public @Nonnull Key key() {
    return PROTECT_ITEM_KEY;
  }

  @Override
  public void updateItem(ItemStack item) {
    //
  }

  @Override
  public void onBlockPlace(BlockPlaceEvent event) {
    sendProtectMessage(event.getPlayer());
    event.setCancelled(true);
  }

  @Override
  public void onUseItem(PlayerInteractEvent event) {
    sendProtectMessage(event.getPlayer());
    event.setCancelled(true);
  }
}
