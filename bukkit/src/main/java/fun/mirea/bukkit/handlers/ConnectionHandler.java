package fun.mirea.bukkit.handlers;

import com.google.common.cache.CacheLoader;
import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler implements Listener {

    private final UserManager userManager = MireaModulePlugin.getUserManager();

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        MireaUser user;
        try {
            user = userManager.getUserCache().get(player.getName());
        } catch (CacheLoader.InvalidCacheLoadException e) {
            user = new MireaUser(player.getName());
            userManager.saveUser(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
