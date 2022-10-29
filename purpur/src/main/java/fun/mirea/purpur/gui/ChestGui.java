package fun.mirea.purpur.gui;

import fun.mirea.purpur.MireaModulePlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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
        inventory = Bukkit.createInventory(null, 9 * rows, Component.text(title));
        slotMap = new HashMap<>();
    }

    @Override
    public void open(Player player) {
        Bukkit.getScheduler().runTask(MireaModulePlugin.getInstance(), () -> player.openInventory(inventory));
    }

    @Override
    public void load() {
        Bukkit.getScheduler().runTask(MireaModulePlugin.getInstance(), () -> slotMap.forEach((slot, content) -> inventory.setItem(slot, content.getItemStack())));
    }

    @Override
    public void addSlot(int slot, GuiSlot guiSlot) {
        slotMap.put(slot, guiSlot);
    }

    @Override
    public CompletableFuture<Void> processEvents(int slot, ClickType clickType) {
        return CompletableFuture.runAsync(() -> {
            if (slotMap.containsKey(slot)) {
                ClickEvents events = slotMap.get(slot).getClickEvents();
                if (events != null) {
                    switch (clickType) {
                        case RIGHT -> {
                            Runnable runnable = events.getRightClickHandler();
                            if (runnable != null)
                                CompletableFuture.runAsync(runnable);
                        }
                        case SHIFT_LEFT -> {
                            Runnable runnable = events.getLeftShiftClickHandler();
                            if (runnable != null)
                                CompletableFuture.runAsync(runnable);
                        }
                        case SHIFT_RIGHT -> {
                            Runnable runnable = events.getRightShiftClickHandler();
                            if (runnable != null)
                                CompletableFuture.runAsync(runnable);
                        }
                        case MIDDLE -> {
                            Runnable runnable = events.getMiddleClickHandler();
                            if (runnable != null)
                                CompletableFuture.runAsync(runnable);
                        }
                        default -> {
                            Runnable runnable = events.getLeftClickHandler();
                            if (runnable != null)
                                CompletableFuture.runAsync(runnable);
                        }
                    }
                }
            }
        });
    }
}
