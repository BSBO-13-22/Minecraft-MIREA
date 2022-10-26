package fun.mirea.purpur.handlers;

import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.scoreboard.UniversityScoreboard;
import fun.mirea.purpur.utility.ComponentUtils;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UserManager;
import fun.mirea.purpur.utility.SkinApplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutionException;

public class ConnectionHandler implements Listener {

    private final UserManager<Player> userManager;
    private final UniversityScoreboard universityScoreboard;

    private final Component motd = Component.text()
            .append(Component.newline()).append(Component.space())
            .append(Component.text("Добро пожаловать!", TextColor.fromHexString("#196bb1")).decorate(TextDecoration.BOLD))
            .append(Component.newline()).append(Component.newline()).append(Component.space())
            .append(FormatUtils.colorize("&8● &fГлавное меню сервера:")
                    .append(Component.space())
                    .append(Component.text("/menu", TextColor.fromHexString("#fba71b"))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/menu"))
                    .hoverEvent(HoverEvent.showText(Component.text("▶ Использовать", TextColor.fromHexString("#FFFF55"))))))
            .append(Component.newline()).append(Component.space())
            .append(FormatUtils.colorize("&8● &fКарточка студента:")
                    .append(Component.space())
                    .append(Component.text("/card help", TextColor.fromHexString("#fba71b"))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/card help"))
                    .hoverEvent(HoverEvent.showText(Component.text("▶ Использовать", TextColor.fromHexString("#FFFF55"))))))
            .append(Component.newline()).append(Component.space())
            .append(FormatUtils.colorize("&8● &fОнлайн крата:")
                    .append(Component.space())
                    .append(Component.text("map.mirea.fun", TextColor.fromHexString("#fba71b"))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://map.mirea.fun"))
                    .hoverEvent(HoverEvent.showText(Component.text("▶ Открыть сайт", TextColor.fromHexString("#FFFF55"))))))
            .append(Component.newline()).append(Component.newline()).append(Component.text("§r      "))
            .append(Component.text("Благодарим за визит!", TextColor.fromHexString("#AAAAAA")).decorate(TextDecoration.ITALIC))
            .append(Component.newline()).append(Component.space())
            .build();

    public ConnectionHandler(UserManager<Player> userManager, UniversityScoreboard universityScoreboard) {
        this.userManager = userManager;
        this.universityScoreboard = universityScoreboard;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        userManager.getCache().get(player.getName()).ifPresentOrElse(user -> {
            event.joinMessage(getConnectionMessage(user, true));
            universityScoreboard.addUser(user);
            if (user.hasSkinData()) {
                SkinApplier skinApplier = new SkinApplier(user.getPlayer());
                skinApplier.process(user.getSkinData());
            }
        }, () -> {
            MireaUser<Player> user = new MireaUser<>(player.getName());
            user.setProvider(userManager.getProvider());
            user.create(userManager);
            event.joinMessage(getConnectionMessage(user, true));
            universityScoreboard.addUser(user);
        });
        Bukkit.getScheduler().runTaskLater(MireaModulePlugin.getInstance(), () -> player.sendMessage(motd), 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        universityScoreboard.removePlayer(player);
        userManager.getCache().get(player.getName()).ifPresentOrElse(user -> event.quitMessage(getConnectionMessage(user, false)), () -> event.quitMessage(null));
    }

    private Component getConnectionMessage(MireaUser<Player> user, boolean join) {
        return ComponentUtils.createDisplayName(user, true).append(Component.space()).append(Component.text(join ? "подключился к серверу!" : "отключился от сервера.").color(TextColor.fromHexString("#AAAAAA")));
    }
}
