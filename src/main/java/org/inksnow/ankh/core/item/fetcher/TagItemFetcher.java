package org.inksnow.ankh.core.item.fetcher;

import lombok.val;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.item.ItemFetcher;
import org.inksnow.ankh.core.api.item.ItemTagger;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class TagItemFetcher implements ItemFetcher {
  private static final ItemTagger itemTagger = AnkhServiceLoader.service(ItemTagger.class);

  @Override
  public @Nonnull List<Key> fetchItem(@Nonnull ItemStack itemStack) {
    val tag = itemTagger.getTag(itemStack);
    if (tag == null) {
      return Collections.emptyList();
    } else {
      return Collections.singletonList(tag);
    }
  }
}
