package dev.piglin.piglinworldapi.util;

import net.melion.rgbchat.api.RGBApi;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
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
    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    public static @Nullable String replace(@Nullable String target, @Nullable HashMap<String, String> replacements, @Nullable OfflinePlayer player) {
        if(target == null) return null;
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

    /**
     * Replaces the string with the specified replacements, PlaceholderAPI (if present) and colors including RGB and RGB gradients
     *
     * @param target       The string
     * @param player       The player to set placeholder against (if using PlaceholderAPI)
     * @param replacements The replacements. {first} will be replaced with second, {third} with fourth and so on
     * @return String with all placeholders set and translated colors (including RGB)
     */
    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    public static String replace(@Nullable String target, @Nullable OfflinePlayer player, String... replacements) {
        Validate.isTrue(replacements.length % 2 == 0);
        var map = new HashMap<String, String>();
        for (int i = 0; i < replacements.length; i += 2) {
            map.put(replacements[i], replacements[i + 1]);
        }
        return replace(target, map, player);
    }
}
