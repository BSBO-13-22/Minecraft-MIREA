package fun.mirea.common.server;

import lombok.Getter;
import lombok.Setter;

public class SerializableLocation {

    @Getter @Setter
    protected String world;

    @Getter @Setter
    protected double x;

    @Getter @Setter
    protected double y;

    @Getter @Setter
    protected double z;

    @Getter @Setter
    protected float yaw;

    @Getter @Setter
    protected float pitch;

    public SerializableLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public <T> T deserialize(Class<T> type) {
        return type.cast(new org.bukkit.Location(org.bukkit.Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }
}
