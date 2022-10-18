package fun.mirea.bukkit.handlers;

import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.bukkit.gui.GuiManager;
import fun.mirea.bukkit.gui.InventoryGui;
import fun.mirea.common.user.UserManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ExecutionException;

public class GuiHandler implements Listener {

    private final GuiManager guiManager;

    public GuiHandler(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws ExecutionException, InterruptedException {
        Player player = (Player) event.getWhoClicked();
        for (InventoryGui gui : guiManager.getAllGui(player.getName())) {
            Inventory inventory = gui.getInventory();
            if (inventory.getViewers().contains(player)) {
                event.setCancelled(true);
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null && itemStack.getType() != Material.AIR)
                    gui.processEvents(event.getSlot(), event.getClick()).get();
            }
        }
    }
}
