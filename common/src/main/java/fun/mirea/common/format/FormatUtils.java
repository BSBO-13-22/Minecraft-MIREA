package fun.mirea.common.format;

import fun.mirea.common.format.Patterns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    public static final char COLOR_CHAR = '§';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&[0-9Aa-fA-Fk-oK-ORrXx]|&#[a-fA-F0-9]{6}");

    public static Component colorize(String contents) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        String[] contentParts = contents.split(" ");
        String lastColor = "&r";
        for (int i = 0; i < contentParts.length; i++) {
            String content = contentParts[i];
            List<String> codes = getColorCodes(content);
            StringBuilder currentColor = new StringBuilder();
            for (String code : codes) {
                if (content.startsWith(currentColor + code))
                    currentColor.append(code);
            }
            if (currentColor.isEmpty())
                currentColor = new StringBuilder(lastColor);
            Matcher hexMatcher = Patterns.HEX.matcher(currentColor);
            if (hexMatcher.matches()) {
                builder.append(Component.text((i == 0 ? "" : " ") + content.replace(currentColor, ""))
                        .color(TextColor.fromHexString(currentColor.substring(1))));
            } else builder.append(Component.text((i == 0 ? "" : " ") + translateAlternateColorCodes(currentColor + content)));

            if (!codes.isEmpty()) {
                StringBuilder endColor = new StringBuilder();
                for (int m = codes.size() - 1; m > 0; m--) {
                    if (content.endsWith(codes.get(m)))
                        endColor.append(codes.get(codes.size() - m));
                }
                lastColor = endColor.toString();
                if (lastColor.isEmpty())
                    lastColor = codes.get(codes.size() - 1);
            }
        }
        return builder.build();
    }

    private static List<String> getColorCodes(String input) {
        List<String> codes = new ArrayList<>();
        Matcher codeMatcher = COLOR_CODE_PATTERN.matcher(input);
        while (codeMatcher.find()) {
            String code = input.substring(codeMatcher.start(), codeMatcher.end());
            codes.add(code);
            input = input.replaceFirst(code, "");
            codeMatcher = COLOR_CODE_PATTERN.matcher(input);
        }
        return codes;
    }

    public static String translateAlternateColorCodes(String textToTranslate) {
        char[] chars = textToTranslate.toCharArray();
        for (int i = 0; i < chars.length - 1; i++ ) {
            if ( chars[i] == '&' && ALL_CODES.indexOf(chars[i + 1]) > -1 ) {
                chars[i] = COLOR_CHAR;
                chars[i + 1] = Character.toLowerCase( chars[i + 1] );
            }
        }
        return new String(chars);
    }

    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
