package org.inksnow.ankh.neigeitems;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.item.ItemFetcher;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Singleton
public class NeigeItemFetcher implements ItemFetcher {
  @Override
  public @Nonnull List<Key> fetchItem(@Nonnull ItemStack itemStack) {
    ItemInfo niItemInfo = ItemManager.INSTANCE.isNiItem(itemStack);
    if (niItemInfo == null) {
      return Collections.emptyList();
    } else {
      return Collections.singletonList(Key.key(
          "NeigeItems".toLowerCase(Locale.ENGLISH),
          niItemInfo.getId().toLowerCase(Locale.ENGLISH)
      ));
    }
  }
}
