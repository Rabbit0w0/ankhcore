package org.inksnow.ankh.core.item.tagger;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.inksnow.ankh.core.api.AnkhCore;
import org.inksnow.ankh.core.api.item.ItemTagger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class PdcItemTagger implements ItemTagger {
  private static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(AnkhCore.PLUGIN_ID, "item-id");

  @Override
  public void setTag(@Nonnull ItemStack itemStack, @Nullable Key itemId) {
    val itemMeta = itemStack.getItemMeta();
    if(itemId == null){
      itemMeta.getPersistentDataContainer().remove(ITEM_ID_KEY);
    }else if(itemMeta == null){
      throw new UnsupportedOperationException("item "+itemStack.getType()+" can't be tagged");
    }else{
      itemMeta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId.asString());
    }
  }

  @Override
  public @Nullable Key getTag(@Nonnull ItemStack itemStack) {
    val itemMeta = itemStack.getItemMeta();
    if(itemMeta == null){
      return null;
    }
    val idString = itemMeta.getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);
    if(idString == null){
      return null;
    }
    return Key.key(idString);
  }
}
