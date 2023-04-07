package org.inksnow.ankh.core.api.item;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface AnkhItemService {
  List<Key> fetchItem(@Nonnull ItemStack itemStack);
  void tagItem(@Nonnull ItemStack stack, Key tag);
}
