package fun.mirea.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import fun.mirea.bukkit.utility.FormatUtils;
import org.bukkit.entity.Player;

public class HelpCommand extends BaseCommand {


    @CommandAlias("info|information|инфо")
    public void onInfo(Player sender) {
        sender.sendMessage(FormatUtils.colorize("&#cb5937Ку-ку!"));
    }

}
