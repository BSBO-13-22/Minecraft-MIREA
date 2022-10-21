package fun.mirea.purpur.handlers;

import fun.mirea.purpur.scoreboard.UniversityScoreboard;
import fun.mirea.purpur.utility.FormatUtils;
import fun.mirea.common.user.university.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.UniversityData;
import fun.mirea.common.user.UserManager;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutionException;

public class ConnectionHandler implements Listener {

    private final UserManager<Player> userManager;
    private final UniversityScoreboard universityScoreboard;


    public ConnectionHandler(UserManager<Player> userManager, UniversityScoreboard universityScoreboard) {
        this.userManager = userManager;
        this.universityScoreboard = universityScoreboard;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        event.joinMessage(getConnectionMessage(userManager.getUserCache().get(player.getName()), true));
        universityScoreboard.addPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        event.quitMessage(getConnectionMessage(userManager.getUserCache().get(player.getName()), false));
        universityScoreboard.removePlayer(player);
    }

    private BuildableComponent<TextComponent, TextComponent.Builder> getConnectionMessage(MireaUser<Player> user, boolean join) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
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
    }
}
