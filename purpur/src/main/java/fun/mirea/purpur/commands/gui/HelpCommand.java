package fun.mirea.purpur.commands.gui;

import co.aikar.commands.annotation.CommandAlias;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import fun.mirea.purpur.gui.ChestGui;
import fun.mirea.purpur.gui.GuiSlot;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;


public class HelpCommand extends BukkitMireaCommand {

    private static final GuiSlot borderSlot = GuiSlot.builder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("&8*").build();

    private static final GuiSlot infoSlot = GuiSlot.builder(Material.WRITABLE_BOOK)
            .setDisplayName("&9&l&oО &9&l&oпроекте")
            .appendLore(Component.empty())
            .appendLore("&r &8● &fПроект создан на некоммерческой основе с &r")
            .appendLore("&r   &fцелью объединить ценителей кубической &r")
            .appendLore("&r   &fпесочницы среди всех студентов &#196bb1МИРЭА &r")
            .appendLore(Component.empty())
            .appendLore("&r &8● &fТематика сервера — &bванильное выживание &r")
            .appendLore("&r   &fс небольшими геймплейными &6дополнениями &r")
            .appendLore(Component.empty())
            .appendLore("&r &8● &fИгровая платформа разрабатывается на &#c56304Java§f: &r")
            .appendLore("&r   &ehttps://github.com/BSBO-13-22/Minecraft-MIREA &r")
            .appendLore(Component.empty())
            .build();

    private static final GuiSlot commandsSlot = GuiSlot.builder(Material.COMMAND_BLOCK)
            .setDisplayName("&a&l&oДоступные &a&l&oкоманды &r")
            .appendLore(Component.empty())
            .appendLore("&r &8● &fКарточка студента: &e/card help &r")
            .appendLore("&r &8● &fРасписание занятий: &e/schedule &r")
            .appendLore("&r &8● &fУстановить скин: &e/skin <ник | url> &r")
            .appendLore("&r &8● &fТелепортация к игроку: &e/tpa <ник> &r")
            .appendLore("&r &8● &fУстановить варп: &e/setwarp <варп> &r")
            .appendLore("&r &8● &fПереместиться на варп: &e/warp <варп> &r")
            .appendLore("&r &8● &fУстановить локацию дома: &e/sethome &r")
            .appendLore("&r &8● &fПереместиться к себе домой: &e/home &r")
            .appendLore(Component.empty())
            .build();

    private static final GuiSlot creditSlot = GuiSlot.builder(Material.PUFFERFISH)
            .setDisplayName(Component.text("Титры ", TextColor.fromHexString("#e7d1a6")).decorate(TextDecoration.BOLD))
            .appendLore(Component.empty())
            .appendLore("&r &8● &fСтоимость аренды оборорудования для &r &r")
            .appendLore("&r   &fбесперебойной работы сервера &c999₽/мес. &r")
            .appendLore("&r   &fПоддеражть проект: &#ff8d08qiwi.com/n/KAPDOR &r")
            .appendLore(Component.empty())
            .build();

    @CommandAlias("menu|info|help")
    public void onMenuCommand(MireaUser<Player> user) {
        ChestGui chestGui = new ChestGui("Главное меню", 3);
        for (int i = 0; i < 27; i++)
            chestGui.addSlot(i, borderSlot);
        chestGui.addSlot(11, infoSlot);
        chestGui.addSlot(13, commandsSlot);
        chestGui.addSlot(15, creditSlot);
        guiManager.saveGui(user.getName(), "main", chestGui);
        chestGui.open(user.getPlayer());
        chestGui.load();
    }
}
