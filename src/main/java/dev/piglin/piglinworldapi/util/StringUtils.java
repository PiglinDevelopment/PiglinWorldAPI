package dev.piglin.piglinworldapi.util;

import net.melion.rgbchat.api.RGBApi;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {
    /**
     * Replaces the string with the specified replacements, PlaceholderAPI (if present) and colors including RGB and RGB gradients
     *
     * @param target       The string
     * @param replacements The replacements. {key} will be replaced with value
     * @param player       The player to set placeholder against (if using PlaceholderAPI)
     * @return String with all placeholders set and translated colors (including RGB)
     */
    public static String replace(String target, @Nullable HashMap<String, String> replacements, @Nullable OfflinePlayer player) {
        target = ChatColor.translateAlternateColorCodes('&', RGBApi.INSTANCE.toColoredMessage(target));
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                target = target.replaceAll("\\{" + entry.getKey() + "}", entry.getValue());
            }
        }
        if (player != null) {
            target = PluginsUtils.placeholderAPITrySetPlaceholders(player, target);
        }
        return target;
    }
}
