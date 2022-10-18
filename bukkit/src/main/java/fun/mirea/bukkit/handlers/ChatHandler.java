package fun.mirea.bukkit.handlers;

import fun.mirea.bukkit.MireaModulePlugin;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ChatHandler implements Listener {

    private static final Pattern mentionPattern = Pattern.compile("@[A-Za-z0-9_]{3,16}");

    private final UserManager userManager;

    public ChatHandler(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) throws ExecutionException, InterruptedException {
        if (!event.isCancelled()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            //event.setMessage(FormatUtils.colorize(processMentions(player.getName(), event.getMessage()).get()));
            MireaUser user = userManager.getUserCache().get(player.getName());
            Component formatComponent = getJsonFormat(user, event.getMessage()).get();
            Bukkit.getOnlinePlayers().forEach(online -> {
                online.sendMessage(formatComponent);
            });
//        String chatPrefix = "&7";
//        String nameColor = "&7";
//        if (user.hasUniversityData()) {
//            Institute institute = Institute.of(user.getUniversityData().getInstitute());
//            if (institute != null) {
//                chatPrefix = institute.getPrefix();
//                nameColor = "&" + institute.getColorScheme();
//            }
//        }
//        String date = new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
//        event.setFormat(
//                FormatUtils.colorize("&8[" + date + "] ") +
//                        FormatUtils.colorize(chatPrefix) + " " +
//                        FormatUtils.colorize(nameColor) +
//                        FormatUtils.colorize(user.getName() + "&7: &f") +
//                        event.getMessage());
        }
    }

    private CompletableFuture<BuildableComponent> getJsonFormat(MireaUser sender, String message) {
        return CompletableFuture.supplyAsync(() -> {
            ComponentBuilder builder = Component.text();
            UniversityData universityData = sender.getUniversityData();
            TextComponent timeComponent = Component.text(new SimpleDateFormat("[HH:mm] ").format(System.currentTimeMillis())).color(TextColor.color(85, 85, 85));
            String chatPrefix = "";
            String colorScheme = "#AAAAAA";
            ComponentBuilder<TextComponent, TextComponent.Builder> prefixBuilder = Component.text();
            if (universityData != null) {
                Institute institute = Institute.of(sender.getUniversityData().getInstitute());
                if (institute != null && institute != Institute.UNKNOWN) {
                    chatPrefix = institute.getPrefix() + " ";
                    colorScheme = institute.getColorScheme();
                }
                TextComponent hoverPrefixComponent = Component.text(FormatUtils.colorize("&7Институт: &f" + universityData.getInstitute()
                        + "\n&7Группа: &f" + universityData.getGroupName() + " &8(" + universityData.getGroupSuffix() + ")"));
                prefixBuilder.hoverEvent(HoverEvent.showText(hoverPrefixComponent));
            }
            prefixBuilder.append(Component.text(chatPrefix)).color(TextColor.fromHexString(colorScheme)).decorate(TextDecoration.BOLD);
            TextComponent nameComponent = Component.text(sender.getName()).color(TextColor.fromHexString(colorScheme));
            TextComponent messageComponent = Component.text(": ").color(TextColor.fromHexString("#AAAAAA")).
                    toBuilder().append(Component.text(ChatColor.translateAlternateColorCodes('&', message))).build();
            return builder.append(timeComponent).append(prefixBuilder.build()).append(nameComponent).append(messageComponent).build();
        });
    }

//    private CompletableFuture<String> processMentions(String sender, final String message) {
//        return CompletableFuture.supplyAsync(() -> {
//            String formattedMessage = message;
//            Matcher matcher = mentionPattern.matcher(message);
//            Set<String> names = new HashSet<>();
//            while (matcher.find()) {
//                String mention = message.substring(matcher.start(), matcher.end());
//                String mentionColor = "&7";
//                try {
//                    MireaUser mentionedUser = userManager.getUserCache().get(mention.substring(1));
//                    if (mentionedUser.hasUniversityData()) {
//                        Institute institute = Institute.of(mentionedUser.getUniversityData().getInstitute());
//                        if (institute != null)
//                            mentionColor = "&" + institute.getColorScheme();
//                    }
//                } catch (ExecutionException ignored) {
//                }
//                formattedMessage = formattedMessage.replace(mention, mentionColor + mention);
//                names.add(mention.substring(1));
//            }
//            String mentorPrefix = "&7";
//            String mentorColor = "&7";
//            try {
//                MireaUser mentorUser = userManager.getUserCache().get(sender);
//                if (mentorUser.hasUniversityData()) {
//                    Institute institute = Institute.of(mentorUser.getUniversityData().getInstitute());
//                    if (institute != null) {
//                        mentorPrefix = institute.getPrefix();
//                        mentorColor = "&" + institute.getColorScheme();
//                    }
//                }
//            } catch (ExecutionException ignored) {
//            }
//            String finalMentorPrefix = mentorPrefix;
//            String finalMentorColor = mentorColor;
//            names.forEach(name -> {
//                Player player = Bukkit.getPlayerExact(name);
//                if (player != null) {
//                    player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
//                    player.sendActionBar(FormatUtils.colorize(finalMentorPrefix) + FormatUtils.colorize(finalMentorColor) + sender + FormatUtils.colorize("&7 упомянул Вас в чате!"));
//                }
//            });
//            return formattedMessage;
//        }, MireaModulePlugin.getThreadManager().getExecutorService());
//    }

}
