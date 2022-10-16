package fun.mirea.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;

public interface InventoryGui {

    void open(Player player);

    void addSlot(int x, int y, GuiSlot guiSlot);

    CompletableFuture<Void> processEvents(int slot, ClickType clickType);

    Inventory getInventory();

}
