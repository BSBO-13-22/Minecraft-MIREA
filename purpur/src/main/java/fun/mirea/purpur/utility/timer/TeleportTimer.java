package fun.mirea.purpur.utility.timer;

import fun.mirea.common.format.MireaComponent;
import fun.mirea.purpur.MireaModulePlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class TeleportTimer {

    private Player player;
    private final Location targetLocation;
    private final Location lastLocation;
    private int taskId;
    private int timer = 3;

    public TeleportTimer(Player player, Location targetLocation) {
        this.player = player;
        this.targetLocation = targetLocation;
        this.lastLocation = player.getLocation().clone();
    }

    public void start(boolean bypass) {
        if (!bypass) {
            taskId = Bukkit.getScheduler().runTaskTimer(MireaModulePlugin.getInstance(), () -> {
                player = Bukkit.getPlayerExact(player.getName());
                if (player != null && player.isOnline()) {
                    if (timer > 0) {
                        Location loc = player.getLocation();
                        if (loc.distance(lastLocation) <= 1) {
                            player.sendActionBar(new MireaComponent(MireaComponent.Type.SUCCESS, "Не двигайтесь! Телепортация через &e" + timer));
                            timer--;
                        } else cancel(false);
                    } else {
                        teleport();
                        cancel(true);
                    }
                } else cancel(false);
            }, 0L, 20L).getTaskId();
        } else teleport();
    }
    public void cancel(boolean success) {
        Bukkit.getScheduler().cancelTask(taskId);
        if (!success && player.isOnline()) player.sendActionBar(new MireaComponent(MireaComponent.Type.ERROR, "Телепортация отменена!"));
        cancelCallback();
    }

    private void teleport() {
        player.sendActionBar(Component.empty());
        player.teleport(targetLocation);
        cancel(true);
        teleportCallback();
    }

    public abstract void teleportCallback();

    public abstract void cancelCallback();
}
