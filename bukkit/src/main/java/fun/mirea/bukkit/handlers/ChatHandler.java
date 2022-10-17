package fun.mirea.bukkit.handlers;

import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.bukkit.utility.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHandler implements Listener {

    private static final Pattern mentionPattern = Pattern.compile("@[A-Za-z0-9_]{3,16}");

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getPlayer();
        event.setMessage(FormatUtils.colorize(processMentions(player.getName(), event.getMessage()).get()));
    }

    private CompletableFuture<String> processMentions(String sender, final String message) {
        return CompletableFuture.supplyAsync(() -> {
            String formattedMessage = message;
            Matcher matcher = mentionPattern.matcher(message);
            Set<String> names = new HashSet<>();
            while (matcher.find()) {
                String mention = message.substring(matcher.start(), matcher.end());
                formattedMessage = formattedMessage.replace(mention, "&7&l" + mention + "&r");
                names.add(mention.substring(1));
            }
            names.forEach(name -> {
                Player player = Bukkit.getPlayerExact(name);
                if (player != null) {
                    player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.sendActionBar(sender + " упомянул Вас в чате!");
                }
            });
            return formattedMessage;
        }, MireaModulePlugin.getThreadManager().getExecutorService());
    }
}
