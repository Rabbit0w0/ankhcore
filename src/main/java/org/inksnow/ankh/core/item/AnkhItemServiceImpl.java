package org.inksnow.ankh.core.item;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.item.*;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptEvent;
import org.inksnow.ankh.core.item.fetcher.TagItemFetcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Singleton
@Slf4j
public class AnkhItemServiceImpl implements AnkhItemService {
  private static final ItemTagger itemTagger = AnkhServiceLoader.service(ItemTagger.class);
  private static final List<ItemFetcher> itemFetcherList = AnkhServiceLoader.serviceList(ItemFetcher.class);

  private final AnkhItemRegistry itemRegistry;

  @Inject
  private AnkhItemServiceImpl(AnkhItemRegistry itemRegistry) {
    this.itemRegistry = itemRegistry;
  }

  @Override
  public @Nonnull Set<Key> fetchTag(@Nonnull ItemStack itemStack) {
    val keys = new LinkedHashSet<Key>();
    for (val itemFetcher : itemFetcherList) {
      keys.addAll(itemFetcher.fetchItem(itemStack));
    }
    return Collections.unmodifiableSet(keys);
  }

  public @Nullable Key tagItem(@Nonnull ItemStack stack) {
    return itemTagger.getTag(stack);
  }

  @Override
  public void tagItem(@Nonnull ItemStack stack, @Nullable Key tag) {
    itemTagger.setTag(stack, tag);
  }

  @SubscriptEvent(priority = EventPriority.HIGHEST)
  private void onInteractEvent(PlayerInteractEvent event) {
    if (event.getAction() == Action.PHYSICAL) {
      return;
    }
    val item = event.getItem();
    val ankhItem = warpItem(item);
    if (ankhItem == null) {
      return;
    }
    ankhItem.acceptInteractEvent(event);
  }

  @SubscriptEvent(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  private void onBlockPlace(BlockPlaceEvent event) {
    val item = event.getItemInHand();
    val ankhItem = warpItem(item);
    if (ankhItem != null) {
      ankhItem.onBlockPlace(event);
    }
  }

  private AnkhItem warpItem(ItemStack item) {
    if (item == null) {
      return null;
    }
    if (item.getType() == Material.AIR) {
      return null;
    }
    for (val itemFetcher : itemFetcherList) {
      for (Key key : itemFetcher.fetchItem(item)) {
        val ankhItem = itemRegistry.get(key);
        if (ankhItem != null) {
          return ankhItem;
        } else if (itemFetcher instanceof TagItemFetcher) {
          logger.warn("No ankh-item {} found, maybe some extensions not loaded", key);
          return ProtectDataItem.instance();
        }
      }
    }
    return null;
  }
}
