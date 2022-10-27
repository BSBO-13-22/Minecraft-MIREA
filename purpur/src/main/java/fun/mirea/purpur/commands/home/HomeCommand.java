package fun.mirea.purpur.commands.home;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.server.SerializableLocation;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.utility.timer.TeleportTimer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends BaseCommand {

    private final List<String> timedPlayers;

    public HomeCommand() {
        this.timedPlayers = new ArrayList<>();
    }

    @CommandAlias("home")
    public void onHomeCommand(MireaUser<Player> user, @Optional String target) {
        Player player = user.getPlayer();
        if (target == null) {
            SerializableLocation homeLocation = user.getHomeLocation();
            if (homeLocation != null) {
                if (!timedPlayers.contains(player.getName())) {
                    Location location = homeLocation.deserialize(Location.class);
                    TeleportTimer timer = new TeleportTimer(player, location) {
                        @Override
                        public void teleportCallback() {
                            player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 1);
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
            } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Ваша точка дома ещё не установлена!"));
        }
    }
}
