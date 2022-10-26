package fun.mirea.purpur.warps;

import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.MireaModulePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Warp {

    @Getter
    private final String name;
    private final String creator;
    private final WarpLocation location;
    @Getter
    private final long creationDate;

    protected Warp(String name, String creator, Location location) {
        this.name = name;
        this.creator = creator;
        this.location = WarpLocation.wrap(location);
        this.creationDate = System.currentTimeMillis();
    }

    protected Warp(String name, String creator, WarpLocation location) {
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
        return location.asBukkit();
    }

    public static class WarpLocation {

        protected static WarpLocation wrap(Location location) {
            return new WarpLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }

        @Getter @Setter
        private String world;

        @Getter @Setter
        private double x;

        @Getter @Setter
        private double y;

        @Getter @Setter
        private double z;

        @Getter @Setter
        private float yaw;

        @Getter @Setter
        private float pitch;

        protected WarpLocation(String world, double x, double y, double z, float yaw, float pitch) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public Location asBukkit() {
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        }
    }

}
