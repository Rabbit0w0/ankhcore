package org.inksnow.ankh.core.item;

import lombok.val;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.AnkhServiceLoader;
import org.inksnow.ankh.core.api.item.AnkhItemService;
import org.inksnow.ankh.core.api.item.ItemFetcher;
import org.inksnow.ankh.core.api.item.ItemTagger;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class AnkhItemServiceImpl implements AnkhItemService {
  private static final ItemTagger itemTagger = AnkhServiceLoader.configLoadService(ItemTagger.class);
  private final List<ItemFetcher> itemFetcherList = new CopyOnWriteArrayList<>();

  @Override
  public List<Key> fetchItem(@Nonnull ItemStack itemStack) {
    val keys = new ArrayList<Key>();
    for (val itemFetcher : itemFetcherList) {
      keys.addAll(itemFetcher.fetchItem(itemStack));
    }
    return Collections.unmodifiableList(keys);
  }

  @Override
  public void tagItem(@Nonnull ItemStack stack, Key tag) {
    itemTagger.setTag(stack, tag);
  }
}
