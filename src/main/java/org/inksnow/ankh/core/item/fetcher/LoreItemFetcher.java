package org.inksnow.ankh.core.item.fetcher;

import lombok.val;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.item.ItemFetcher;
import org.inksnow.ankh.core.common.config.AnkhConfig;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class LoreItemFetcher implements ItemFetcher {
  private final String tagStart;
  private final String tagEnd;
  private final int tagStartLength;

  @Inject
  private LoreItemFetcher(AnkhConfig ankhConfig) {
    val config = ankhConfig.item().loreFetcher();
    this.tagStart = config.markStart();
    this.tagEnd = config.markEnd();
    tagStartLength = this.tagStart.length();
  }

  @Override
  public @Nonnull List<Key> fetchItem(@Nonnull ItemStack itemStack) {
    if (!itemStack.hasItemMeta()) {
      return Collections.emptyList();
    }
    val itemMeta = itemStack.getItemMeta();
    if (itemMeta == null || !itemMeta.hasLore()) {
      return Collections.emptyList();
    }
    val itemLore = itemMeta.lore();
    if (itemLore == null) {
      return Collections.emptyList();
    }
    for (Component component : itemLore) {
      val loreLine = PlainComponentSerializer.plain().serialize(component);
      val ankhStart = loreLine.indexOf(tagStart);
      if (ankhStart == -1) {
        continue;
      }
      val ankhEnd = loreLine.indexOf(tagEnd, ankhStart + 1);
      if (ankhEnd == -1) {
        continue;
      }
      val itemId = loreLine.substring(ankhStart + tagStartLength, ankhEnd);
      try {
        return Collections.singletonList(Key.key(itemId));
      } catch (InvalidKeyException e) {
        //
      }
    }
    return Collections.emptyList();
  }
}
