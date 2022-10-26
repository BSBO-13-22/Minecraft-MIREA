package fun.mirea.purpur.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.format.FormatUtils;
import org.bukkit.entity.Player;

public class HelpCommand extends BaseCommand {


    @CommandAlias("info|information|инфо")
    public void onInfo(MireaUser<Player> user) {
        user.getPlayer().sendMessage(FormatUtils.colorize("&#cb5937Ку-ку!"));
    }

}
