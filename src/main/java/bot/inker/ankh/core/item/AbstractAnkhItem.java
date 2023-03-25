package bot.inker.ankh.core.item;

import bot.inker.ankh.core.api.item.AnkhItem;
import bot.inker.ankh.core.libs.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    if (!key().asString().equals(nbtItem.getString(ITEM_ID_TAG))) {
      nbtItem.setString(ITEM_ID_TAG, key().asString());
    }
    onUpdateItemNbt(nbtItem);
    nbtItem.applyNBT(item);
    item.editMeta(meta -> {
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
