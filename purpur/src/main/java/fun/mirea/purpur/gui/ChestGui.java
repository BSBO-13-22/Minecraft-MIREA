package fun.mirea.purpur.gui;

import fun.mirea.purpur.MireaModulePlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChestGui implements InventoryGui {

    @Getter
    private final Inventory inventory;
    private final Map<Integer, GuiSlot> slotMap;

    public ChestGui(String title, int rows) {
        inventory = Bukkit.createInventory(null, 9 * rows, title);
        slotMap = new HashMap<>();
    }

    @Override
    public void open(Player player) {
        Bukkit.getScheduler().runTask(MireaModulePlugin.getInstance(), () -> player.openInventory(inventory));
    }

    @Override
    public void load() {
        Bukkit.getScheduler().runTask(MireaModulePlugin.getInstance(), () -> slotMap.forEach((slot, content) -> inventory.setItem(slot, content.toItemStack())));
    }

    @Override
    public void addSlot(int slot, GuiSlot guiSlot) {
        //int slot = (x - 1) + ((y - 1) * 9);
        //inventory.setItem(slot, guiSlot.toItemStack());
        slotMap.put(slot, guiSlot);
    }

    @Override
    public CompletableFuture<Void> processEvents(int slot, ClickType clickType) {
        return CompletableFuture.runAsync(() -> {
            if (slotMap.containsKey(slot)) {
                ClickEvents events = slotMap.get(slot).getClickEvents();
                switch (clickType) {
                    case RIGHT: {
                        Runnable runnable = events.getRightClickHandler();
                        if (runnable != null)
                            CompletableFuture.runAsync(runnable, MireaModulePlugin.getThreadManager().getExecutorService());
                        break;
                    }
                    case SHIFT_LEFT: {
                        Runnable runnable = events.getLeftShiftClickHandler();
                        if (runnable != null)
                            CompletableFuture.runAsync(runnable, MireaModulePlugin.getThreadManager().getExecutorService());
                        break;
                    }
                    case SHIFT_RIGHT: {
                        Runnable runnable = events.getRightShiftClickHandler();
                        if (runnable != null)
                            CompletableFuture.runAsync(runnable, MireaModulePlugin.getThreadManager().getExecutorService());
                        break;
                    }
                    case MIDDLE: {
                        Runnable runnable = events.getMiddleClickHandler();
                        if (runnable != null)
                            CompletableFuture.runAsync(runnable, MireaModulePlugin.getThreadManager().getExecutorService());
                        break;
                    }
                    default: {
                        Runnable runnable = events.getLeftClickHandler();
                        if (runnable != null)
                            CompletableFuture.runAsync(runnable, MireaModulePlugin.getThreadManager().getExecutorService());
                    }
                }
            }
        }, MireaModulePlugin.getThreadManager().getExecutorService());
    }
}