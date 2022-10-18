package fun.mirea.bukkit.handlers;

import com.google.common.cache.CacheLoader;
import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.bukkit.scoreboard.UniversityScoreboard;
import fun.mirea.bukkit.utility.FormatUtils;
import fun.mirea.common.user.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UniversityData;
import fun.mirea.common.user.UserManager;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler implements Listener {

    private final UserManager userManager;
    private final UniversityScoreboard universityScoreboard;

    public ConnectionHandler(UserManager userManager, UniversityScoreboard universityScoreboard) {
        this.userManager = userManager;
        this.universityScoreboard = universityScoreboard;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getPlayer();
        event.joinMessage(getConnectionMessage(userManager.getUserCache().get(player.getName()), true).get());
        universityScoreboard.addPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getPlayer();
        event.quitMessage(getConnectionMessage(userManager.getUserCache().get(player.getName()), false).get());
        universityScoreboard.removePlayer(player);
    }

    private CompletableFuture<BuildableComponent> getConnectionMessage(MireaUser user, boolean join) {
        return CompletableFuture.supplyAsync(() -> {
            ComponentBuilder builder = Component.text();
            UniversityData universityData = user.getUniversityData();
            String chatPrefix = "";
            String colorScheme = "#AAAAAA";
            ComponentBuilder<TextComponent, TextComponent.Builder> prefixBuilder = Component.text();
            if (universityData != null) {
                Institute institute = Institute.of(user.getUniversityData().getInstitute());
                if (institute != null && institute != Institute.UNKNOWN) {
                    chatPrefix = institute.getPrefix() + " ";
                    colorScheme = institute.getColorScheme();
                }
                TextComponent hoverPrefixComponent = Component.text(FormatUtils.colorize("&7Институт: &f" + universityData.getInstitute()
                        + "\n&7Группа: &f" + universityData.getGroupName() + " &8(" + universityData.getGroupSuffix() + ")"));
                prefixBuilder.hoverEvent(HoverEvent.showText(hoverPrefixComponent));
            }
            prefixBuilder.append(Component.text(chatPrefix)).color(TextColor.fromHexString(colorScheme)).decorate(TextDecoration.BOLD);
            TextComponent nameComponent = Component.text(user.getName()).color(TextColor.fromHexString(colorScheme));
            TextComponent messageComponent = Component.text(" " + (join ? "подключился к серверу!" : "отключился от сервера.")).color(TextColor.fromHexString("#AAAAAA"));
            return builder.append(prefixBuilder.build()).append(nameComponent).append(messageComponent).build();
        });
    }
}
