package org.inksnow.ankh.core.item.debug;

import lombok.val;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.ankh.core.api.AnkhCore;
import org.inksnow.ankh.core.api.plugin.annotations.AutoRegistered;
import org.inksnow.ankh.core.api.world.WorldService;
import org.inksnow.ankh.core.item.AbstractAnkhItem;
import org.inksnow.ankh.core.world.PdcWorldService;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

@AutoRegistered
public class DebugRemoveItem extends AbstractAnkhItem {
  private static final Key ITEM_KEY = Key.key(AnkhCore.PLUGIN_ID, "remove-item");
  private static final Component ITEM_NAME = Component.text()
      .append(AnkhCore.PLUGIN_NAME_COMPONENT)
      .append(Component.text("remove util", NamedTextColor.RED))
      .build();
  private static final List<Component> ITEM_LORE = List.of(
      Component.text("left click to remove ankh-block and block", NamedTextColor.WHITE),
      Component.empty(),
      Component.text("right click to remove ankh-block only", NamedTextColor.WHITE),
      Component.empty(),
      Component.text("This is ankh-core debug item, for internal usage only", NamedTextColor.WHITE),
      Component.text("DON'T GIVE IT TO PLAYER", NamedTextColor.RED)
  );

  private final PdcWorldService worldService;

  @Inject
  private DebugRemoveItem(PdcWorldService worldService) {
    this.worldService = worldService;
  }

  @Override
  public @Nonnull Material material() {
    return Material.STICK;
  }

  @Override
  public @Nonnull Component itemName() {
    return ITEM_NAME;
  }

  @Nonnull
  @Override
  public List<Component> lores() {
    return ITEM_LORE;
  }

  @Override
  public @NonNull Key key() {
    return ITEM_KEY;
  }


  @SuppressWarnings("deprecation") // for debug use only
  @Override
  public void acceptInteractEvent(PlayerInteractEvent event) {
    event.setCancelled(true);
    val action = event.getAction();
    if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
      val clickedBlock = event.getClickedBlock();
      if(clickedBlock == null){
        return;
      }
      worldService.forceRemoveBlock(clickedBlock.getLocation());
      if(action == Action.LEFT_CLICK_BLOCK){
        clickedBlock.setType(Material.AIR);
      }
    }
  }
}
