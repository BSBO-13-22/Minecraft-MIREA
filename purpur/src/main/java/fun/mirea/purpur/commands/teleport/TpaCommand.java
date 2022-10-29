package fun.mirea.purpur.commands.teleport;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import fun.mirea.purpur.utility.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaCommand extends BukkitMireaCommand {

    @CommandAlias("tpa|call")
    @Syntax("<никнейм>")
    @CommandCompletion("@players")
    public void onTpaCommand(MireaUser<Player> user, String target) {
        if (!user.getName().equalsIgnoreCase(target)) {
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer != null) {
                if (teleportManager.addRequest(user.getPlayer(), targetPlayer)) {
                    user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Запрос на телепортацию успешно отправлен!"));
                    targetPlayer.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "")
                            .append(ComponentUtils.createDisplayName(user, true))
                            .append(Component.space())
                            .append(FormatUtils.colorize("&fотправил запрос на телепортацию к Вам! Используйте"))
                            .append(Component.space())
                            .append(Component.text("/tpyes " + user.getName(), TextColor.fromHexString("#FFFF55"))
                                    .decorate(TextDecoration.UNDERLINED)
                                    .clickEvent(ClickEvent.runCommand("/tpyes " + user.getName()))
                                    .hoverEvent(HoverEvent.showText(FormatUtils.colorize("&e▶ Использовать"))))
                            .append(FormatUtils.colorize("&f, чтобы принять его."))
                    );
                    Bukkit.getScheduler().runTaskLater(MireaModulePlugin.getInstance(), () -> {
                        teleportManager.denyRequest(user.getPlayer(), targetPlayer);
                    }, 20L * 120L);
                } else user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Вы уже отправили запрос на телепортацию к этому игроку!"));
            } else user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Этот игрок сейчас не в сети!"));
        } else user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Вы не можете отправить запрос самому себе!"));
    }
}
