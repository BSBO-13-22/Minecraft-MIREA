package fun.mirea.purpur.commands.warp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.warps.WarpManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class DelWarpCommand extends BaseCommand {

    private final WarpManager warpManager;

    public DelWarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @CommandAlias("delwarp")
    @Syntax("<название>")
    @CommandCompletion("@warps")
    public void onDelWarpCommand(MireaUser<Player> user, String name) throws ExecutionException {
        Player player = user.getPlayer();
        warpManager.getCache().get(name.toLowerCase()).ifPresentOrElse(warp -> {
            warp.getCreator().ifPresentOrElse(creator -> {
                if (user.getName().equals(creator.getName()) || player.isOp()) {
                    try {
                        warpManager.unregisterWarp(warp.getName()).get();
                        player.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Варп &e{name}&r был успешно удалён!",
                                new Placeholder("name", warp.getName())));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Варп может удалить только его владелец!"));
            }, () -> player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Не удалось удалить этот варп!")));
        }, () -> player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Варп с таким названием не существует!")));
    }
}
