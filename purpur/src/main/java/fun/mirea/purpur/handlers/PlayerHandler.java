package fun.mirea.purpur.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerHandler implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.deathMessage(null);
    }

}
