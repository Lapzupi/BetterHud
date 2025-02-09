package cz.apigames.betterhud.api.utils;

import cz.apigames.betterhud.api.BetterHudAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    private static final Pattern legacyPattern = Pattern.compile("&[0-9a-fk-or]");
    private static final Pattern hexPattern = Pattern.compile("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})");

    public static @NotNull String colorize(String message) {
        if (BetterHudAPI.isHexSupported()) {
            Matcher matcher = hexPattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start() + 1, matcher.end() - 1);
                message = message.replace(matcher.group(), "" + ChatColor.of(color));
            }

        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String translatePlaceholders(String message, Player player) {
        return translatePlaceholders(message, player, null);
    }

    public static String translatePlaceholders(String message, Player player, List<Placeholder> placeholders) {

        if (BetterHudAPI.isPapiEnabled()
                && PlaceholderAPI.containsPlaceholders(message)) {

            //FIX TO EXCLUDE TRANSLATING COLORS

            Pattern pattern = Pattern.compile("%(\\S*?)%");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String placeholder = PlaceholderAPI.setPlaceholders(player, matcher.group());
                message = message.replace(matcher.group(), placeholder);
            }

        }

        if (placeholders != null) {
            message = Placeholder.replacePlaceholders(placeholders, message);
        }

        for (String placeholder : BetterHudPlaceholders.placeholders) {
            message = message.replace(placeholder, BetterHudPlaceholders.getPlaceholder(placeholder, player));
        }

        return message;
    }

    public static boolean isLegacyColorCode(@NotNull String message, int index) {
        String colorCode = message.substring(index, index + 2);
        Matcher matcher = legacyPattern.matcher(colorCode);
        return matcher.find();
    }

    public static boolean isHexColorCode(@NotNull String message, int index) {
        String colorCode = message.substring(index, index + 9);
        Matcher matcher = hexPattern.matcher(colorCode);
        return matcher.find();
    }

    public static String getCharNameFromPath(@NotNull String path) {

        String[] split = path.split("/");

        return split[split.length - 1].split("\\.")[0];

    }

    public static @NotNull String stripColors(String str) {

        str = str.replaceAll("&[0-9a-fk-or]", "");
        str = str.replaceAll("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})", "");

        return str;

    }

    public static @NotNull String getRawMessage(String str) {

        str = str.replaceAll("%(\\S*?)%", "");
        str = str.replaceAll("\\{(\\S*?)}", "");
        str = str.replaceAll("&[0-9a-fk-or]", "");
        str = str.replaceAll("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})", "");

        return str;
    }

    public static @NotNull String enumToName(@NotNull String enumValue) {
        return enumValue.charAt(0) + enumValue.substring(1).toLowerCase(Locale.ROOT);
    }

}
