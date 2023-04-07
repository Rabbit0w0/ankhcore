package org.inksnow.ankh.core.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.inksnow.ankh.core.api.item.AnkhItem;
import org.inksnow.ankh.core.libs.nbtapi.NBTItem;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractAnkhItem implements AnkhItem {
  public abstract @Nonnull Material material();

  public abstract @Nonnull Component itemName();

  public abstract @Nonnull List<Component> lores();

  public ItemStack createItem() {
    ItemStack itemStack = new ItemStack(Material.CLOCK);
    updateItem(itemStack);
    return itemStack;
  }

  @Override
  public final void updateItem(ItemStack item) {
    item.setType(material());
    NBTItem nbtItem = new NBTItem(item);
    onUpdateItemNbt(nbtItem);
    nbtItem.applyNBT(item);
    item.editMeta(meta -> {
      meta.getPersistentDataContainer()
          .set(ITEM_ID_KEY, PersistentDataType.STRING, key().asString());
      meta.displayName(itemName());
      meta.lore(lores());
      onUpdateItemMeta(meta);
    });

    onUpdateItem(item);
  }

  protected void onUpdateItem(ItemStack item) {
    //
  }

  protected void onUpdateItemNbt(NBTItem nbtItem) {
    //
  }

  protected void onUpdateItemMeta(ItemMeta itemMeta) {
    //
  }
}
