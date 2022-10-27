package fun.mirea.purpur.commands.warp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.utility.timer.TeleportTimer;
import fun.mirea.purpur.warps.WarpManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class WarpCommand extends BaseCommand {

    private final WarpManager warpManager;
    private final List<String> timedPlayers;

    public WarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
        this.timedPlayers = new ArrayList<>();
    }

    @CommandAlias("warp")
    @Syntax("<название>")
    @CommandCompletion("@warps")
    public void onWarpCommand(MireaUser<Player> user, String name) throws ExecutionException {
        Player player = user.getPlayer();
        warpManager.getCache().get(name.toLowerCase()).ifPresentOrElse(warp -> {
            if (warp.getLocation().distanceSquared(player.getLocation()) > 25) {
                if (!timedPlayers.contains(player.getName())) {
                    TeleportTimer timer = new TeleportTimer(player, warp.getLocation()) {
                        @Override
                        public void teleportCallback() {
                            player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 1);
                            ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text()
                                    .append(new MireaComponent(MireaComponent.Type.SUCCESS, "Вы на территории варпа &e{warp}",
                                            new Placeholder("warp", warp.getName())));
                            player.sendActionBar(message.build());
                            timedPlayers.remove(player.getName());
                        }
                        @Override
                        public void cancelCallback() {
                            timedPlayers.remove(player.getName());
                        }
                    };
                    timedPlayers.add(player.getName());
                    timer.start(player.isOp());
                }
            } else player.sendActionBar(new MireaComponent(MireaComponent.Type.ERROR, "Вы уже находитесь на территори варпа!"));
        }, () -> player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Варп с таким названием не существует!")));
    }
}
