package fun.mirea.purpur.handlers;

import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.utility.ComponentUtils;
import fun.mirea.purpur.utility.FormatUtils;
import fun.mirea.common.user.university.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.UniversityData;
import fun.mirea.common.user.UserManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHandler implements Listener {

    private static final Pattern mentionPattern = Pattern.compile("@[A-Za-z0-9_]{3,16}");

    private static final Pattern urlPattern = Pattern.compile("^(https?|http|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern resourcePackPattern = Pattern.compile("<rp[|].*>");

    private final UserManager<Player> userManager;

    public ChatHandler(UserManager<Player> userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) throws ExecutionException {
        if (!event.isCancelled()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            userManager.getUserCache().get(player.getName()).ifPresent(user -> {
                TextComponent component = (TextComponent) event.originalMessage();
                Component formatComponent = getJsonFormat(user, component.content());
                Bukkit.getOnlinePlayers().forEach(online -> online.sendMessage(formatComponent));
                Bukkit.getConsoleSender().sendMessage(formatComponent);
            });
        }
    }

    private BuildableComponent<TextComponent, TextComponent.Builder> getJsonFormat(MireaUser<Player> sender, String message) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        TextComponent timeComponent = Component.text(new SimpleDateFormat("[HH:mm] ").format(System.currentTimeMillis())).color(TextColor.color(85, 85, 85));
        TextComponent messageComponent = Component.text(": ").color(TextColor.fromHexString("#AAAAAA")).
                toBuilder().append(parseMessage(ChatColor.translateAlternateColorCodes('&', message))).build();
        return builder.append(timeComponent).append(ComponentUtils.createDisplayName(sender, true)).append(messageComponent).build();
    }

    private BuildableComponent<TextComponent, TextComponent.Builder> parseMessage(String message) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        for (String part : message.split(" ")) {
            ComponentBuilder<TextComponent, TextComponent.Builder> partBuilder = Component.text();
            Matcher urlMatcher = urlPattern.matcher(part);
            Matcher mentionMatcher = mentionPattern.matcher(part);
            Matcher resourcePackMatcher = resourcePackPattern.matcher(part);
            if (urlMatcher.matches()) {
                builder.append(Component.text(part)
                        .color(TextColor.fromHexString("#ffffff"))
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, part))
                        .hoverEvent(HoverEvent.showText(Component.text("▶ Перейти по ссылке", TextColor.fromHexString("#FFFF55")))));
            } else if (mentionMatcher.matches()) {
                try {
                    userManager.getUserCache().get(part.substring(1)).ifPresentOrElse(mentioned -> {
                        Institute institute =  mentioned.hasUniversityData() ? Institute.of(mentioned.getUniversityData().getInstitute()) : Institute.UNKNOWN;
                        Component tagComponent = Component.text("@" + mentioned.getName()).color(TextColor.fromHexString(institute.getColorScheme()));
                        if (institute != Institute.UNKNOWN) {
                            UniversityData universityData = mentioned.getUniversityData();
                            tagComponent = tagComponent.hoverEvent(HoverEvent.showText(Component.text(FormatUtils.colorize("&7Имя: &f" + mentioned.getStudentName() + "\n&7Институт: &f" + universityData.getInstitute()
                                    + "\n&7Группа: &f" + universityData.getGroupName() + " &8(" + universityData.getGroupSuffix() + ")"))));
                        }
                        partBuilder.append(tagComponent);
                    }, () -> partBuilder.append(Component.text(part)));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else if (resourcePackMatcher.matches()) {
                String url = part.substring(1, part.length() - 1).split("\\|")[1];
                if (urlPattern.matcher(url).matches()) {
                    Component buttonComponent = Component.text("[РЕСУРСПАК]", TextColor.fromHexString("#DDD605"))
                            .hoverEvent(HoverEvent.showText(Component.text("▶ Установить", TextColor.fromHexString("#DDD605"))))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack download " + url));
                    partBuilder.append(buttonComponent);
                } else partBuilder.append(Component.text(part));
            } else partBuilder.append(Component.text(part));
            partBuilder.append(Component.empty().color(TextColor.fromHexString("#AAAAAA")));
            builder.append(partBuilder).append(Component.space());
        }
        return builder.build();
    }
}
