package fun.mirea.purpur.commands.home;

import co.aikar.commands.annotation.CommandAlias;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.server.SerializableLocation;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetHomeCommand extends BukkitMireaCommand {

    @CommandAlias("sethome")
    public void onSetHomeCommand(MireaUser<Player> user) {
        Player player = user.getPlayer();
        Location loc = player.getLocation();
        player.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Точка дома установлена на &eX: {x}, Y: {y}, Z: {z} &7({world})", new Placeholder("x", Math.round(loc.getX())),
                new Placeholder("y", Math.round(loc.getY())), new Placeholder("z", Math.round(loc.getX())), new Placeholder("world", loc.getWorld().getName())));
        SerializableLocation homeLocation = new SerializableLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        user.setHomeLocation(homeLocation);
        user.save(MireaModulePlugin.getUserManager());
    }
}
