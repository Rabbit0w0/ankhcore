package org.inksnow.ankh.core.api.item;

import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;

/**
 * tag item with set and get, load by AnkhServiceLoader
 *
 * @see org.inksnow.ankh.core.api.AnkhServiceLoader
 */
@Named("item-tagger")
public interface ItemTagger {
  /**
   * set tag to ItemStack
   *
   * @param itemStack item to set itemId
   * @param itemId    itemId to set
   * @throws UnsupportedOperationException if item can't set itemId
   */
  void setTag(@Nonnull ItemStack itemStack, @Nullable Key itemId);

  /**
   * get tag from ItemStack
   *
   * @param itemStack item to get itemId
   * @return the itemId if item has, null if no found
   * @throws InvalidKeyException if the namespace or value contains an invalid character
   */
  @Nullable
  Key getTag(@Nonnull ItemStack itemStack);
}
