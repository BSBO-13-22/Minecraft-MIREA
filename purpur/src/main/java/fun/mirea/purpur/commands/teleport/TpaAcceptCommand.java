package fun.mirea.purpur.commands.teleport;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import fun.mirea.purpur.utility.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaAcceptCommand extends BukkitMireaCommand {

    @CommandAlias("tpaccept|tpyes")
    @Syntax("<никнейм>")
    @CommandCompletion("@players")
    public void onTpaCommand(MireaUser<Player> user, String requester) {
        if (!user.getName().equalsIgnoreCase(requester)) {
            Player requesterPlayer = Bukkit.getPlayer(requester);
            if (requesterPlayer != null) {
                if (teleportManager.acceptRequest(requesterPlayer, user.getPlayer())) {
                    requesterPlayer.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "")
                            .append(ComponentUtils.createDisplayName(user, true))
                            .append(Component.space())
                            .append(FormatUtils.colorize("&fпринял Ваш запрос на телепортацию!"))
                    );
                } else
                    user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Нет активных запросов на телепортацию от этого игрока!"));
            } else
                user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Этот игрок сейчас не в сети!"));
        }
    }
}
