package me.zxoir.picksurvivaltwo.util;

import me.zxoir.picksurvivaltwo.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class Colors {
    public static String primary = colorize(ConfigManager.getPrimaryColor());
    public static String secondary = colorize(ConfigManager.getSecondaryColor());
    public static String error = colorize(ConfigManager.getErrorColor());

    public static @NotNull String colorize(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] characters = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char character : characters) {
                builder.append("&").append(character);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
