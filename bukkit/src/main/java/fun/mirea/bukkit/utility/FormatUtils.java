package fun.mirea.bukkit.utility;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    private static final Pattern hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String colorize(String input) {
        Matcher matcher = hexPattern.matcher(input);
        while (matcher.find()) {
            String color = input.substring(matcher.start(), matcher.end());
            if (!color.contains(String.valueOf(org.bukkit.ChatColor.COLOR_CHAR)))
                input = input.replaceFirst(color, "" + ChatColor.of(color.substring(1)));
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
