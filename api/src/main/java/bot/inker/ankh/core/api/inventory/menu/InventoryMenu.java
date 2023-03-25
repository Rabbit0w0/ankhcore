package bot.inker.ankh.core.api.inventory.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface InventoryMenu {
  default void acceptCloseEvent(InventoryCloseEvent event){
    //
  }
  default void acceptClickEvent(InventoryClickEvent event){
    //
  }
  default void acceptDragEvent(InventoryDragEvent event){
    //
  }
}
