package fun.mirea.purpur.commands.warp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.user.MireaUser;
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
        warpManager.getCache().get(name.toLowerCase()).ifPresentOrElse(warp -> player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR,
                        "Варп с названием &6{name} &cуже существует!", new Placeholder("name", warp.getName()))), () -> {
            try {
                warpManager.registerWarp(name, player.getName(), player.getLocation()).get().ifPresentOrElse(warp ->
                                player.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Варп &e{name} &rуспешно создан!",
                                        new Placeholder("name", warp.getName()))),
                        () -> player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Не удалось создать варп! Пожалуйста, сообщите об этом &6@drkapdor")));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
