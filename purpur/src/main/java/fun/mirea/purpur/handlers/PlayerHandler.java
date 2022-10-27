package fun.mirea.purpur.handlers;

import fun.mirea.common.server.SerializableLocation;
import fun.mirea.common.user.UserManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.concurrent.ExecutionException;

public class PlayerHandler implements Listener {

    private final UserManager<Player> userManager;

    public PlayerHandler(UserManager<Player> userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.deathMessage(null);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        if (event.shouldSetSpawnLocation()) {
            userManager.getCache().get(player.getName()).ifPresent(user -> {
                Location bedLoc = event.getBed().getLocation();
                SerializableLocation location = new SerializableLocation(bedLoc.getWorld().getName(), bedLoc.getX(), bedLoc.getY(), bedLoc.getZ(), bedLoc.getYaw(), bedLoc.getPitch());
                user.setHomeLocation(location);
                user.save(userManager);
            });
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        userManager.getCache().get(player.getName()).ifPresent(user -> {
            if (user.getHomeLocation() != null)
                event.setRespawnLocation(user.getHomeLocation().deserialize(Location.class));
        });
    }
}
