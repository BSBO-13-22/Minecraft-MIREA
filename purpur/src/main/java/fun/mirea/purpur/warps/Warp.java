package fun.mirea.purpur.warps;

import fun.mirea.common.server.SerializableLocation;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.MireaModulePlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@NoArgsConstructor
public class Warp {

    @Getter
    private String name;
    private String creator;
    private SerializableLocation location;
    @Getter
    private long creationDate;

    protected Warp(String name, String creator, Location location) {
        this.name = name;
        this.creator = creator;
        this.location = new SerializableLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.creationDate = System.currentTimeMillis();
    }

    protected Warp(String name, String creator, SerializableLocation location) {
        this.name = name;
        this.creator = creator;
        this.location = location;
        this.creationDate = System.currentTimeMillis();
    }

    public Optional<MireaUser<Player>> getCreator() {
        try {
            return MireaModulePlugin.getUserManager().getCache().get(creator);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Location getLocation() {
        return location.deserialize(Location.class);
    }
}
