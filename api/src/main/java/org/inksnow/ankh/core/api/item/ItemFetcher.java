package org.inksnow.ankh.core.api.item;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * fetch item tag from item stack
 */
public interface ItemFetcher {
  /**
   * fetch item tag from item stack
   *
   * @param itemStack the item to get stacks
   * @return tags, empty if no tag found
   */
  @Nonnull
  List<Key> fetchItem(@Nonnull ItemStack itemStack);
}
