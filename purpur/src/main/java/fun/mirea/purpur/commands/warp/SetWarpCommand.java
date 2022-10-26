package fun.mirea.purpur.commands.warp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.warps.Warp;
import fun.mirea.purpur.warps.WarpManager;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class SetWarpCommand extends BaseCommand {

    private final WarpManager warpManager;

    public SetWarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @CommandAlias("setwarp")
    @Syntax("<название>")
    public void onSetWarpCommand(MireaUser<Player> user, String name) throws ExecutionException {
        Player player = user.getPlayer();
        warpManager.getCache().get(name).ifPresentOrElse(warp -> player.sendActionBar(new MireaComponent(MireaComponent.Type.ERROR,
                        "Варп с названием &6{name} &суже существует!", new Placeholder("name", warp.getName()))), () -> {
            Warp warp = warpManager.registerWarp(name, player.getName(), player.getLocation());
            player.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Варп &e{name} &rуспешно создан!", new Placeholder("name", warp.getName())));
        });
    }
}
