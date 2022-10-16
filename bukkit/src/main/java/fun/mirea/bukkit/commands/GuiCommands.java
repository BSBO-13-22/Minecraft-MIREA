package fun.mirea.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.bukkit.gui.ChestGui;
import fun.mirea.bukkit.gui.ClickEvents;
import fun.mirea.bukkit.gui.GuiManager;
import fun.mirea.bukkit.gui.GuiSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GuiCommands extends BaseCommand {

    private GuiManager guiManager = MireaModulePlugin.getGuiManager();

    @CommandAlias("menu|меню")
    public void onMenuCommand(Player player) {
        ChestGui chestGui = new ChestGui("Главное меню", 6);
        chestGui.addSlot(3, 3,
                GuiSlot.builder().material(Material.EMERALD).displayName("&aПривет!").lore(Arrays.asList("&fКак твои дела?", "&fКак настроение?", "&fПошёл нахуй)"))
                        .amount(1).enchanted(false).clickEvents(ClickEvents.builder().leftClickHandler(() -> {
                            player.sendMessage("Пшёл нахуй");
                            player.closeInventory();
                        }).build()).build());
        guiManager.saveGui(player.getName(), "main", chestGui);
        chestGui.open(player);
    }
}
