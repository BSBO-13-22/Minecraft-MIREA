package fun.mirea.purpur.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;

public interface InventoryGui {

    void open(Player player);

    void addSlot(int slot, GuiSlot guiSlot);

    void load();

    CompletableFuture<Void> processEvents(int slot, ClickType clickType);

    Inventory getInventory();

}
