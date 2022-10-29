package fun.mirea.purpur.teleport;

import fun.mirea.purpur.utility.timer.TeleportTimer;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TeleportRequest {

    @Getter
    private final String target;
    @Getter
    private final TeleportTimer timer;
    private final long expiresAt;

    public TeleportRequest(String target, TeleportTimer timer) {
        this.target = target;
        this.timer = timer;
        this.expiresAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

}
