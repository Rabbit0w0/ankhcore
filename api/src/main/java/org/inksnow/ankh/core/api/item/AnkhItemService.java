package org.inksnow.ankh.core.api.item;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface AnkhItemService {
  @Nonnull
  Set<Key> fetchTag(@Nonnull ItemStack itemStack);

  @Nullable
  Key tagItem(@Nonnull ItemStack stack);

  void tagItem(@Nonnull ItemStack stack, @Nullable Key tag);
}
