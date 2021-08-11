package dev.piglin.piglinworldapi.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.melion.rgbchat.api.RGBApi;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern componentPattern = Pattern.compile("\\{([^}]*)}\\(([^)]*)\\)(?:\\[(OPEN_URL|OPEN_FILE|RUN_COMMAND|SUGGEST_COMMAND|CHANGE_PAGE|COPY_TO_CLIPBOARD)\\|([^{]*)])?");

    /**
     * Replaces the string with the specified replacements, PlaceholderAPI (if present) and colors including RGB and RGB gradients
     *
     * @param target       The string
     * @param replacements The replacements. {key} will be replaced with value
     * @param player       The player to set placeholder against (if using PlaceholderAPI)
     * @return String with all placeholders set and translated colors (including RGB)
     */
    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    public static @Nullable String replace(@Nullable String target, @Nullable Map<String, String> replacements, @Nullable OfflinePlayer player) {
        if (target == null) return null;
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

    /**
     * Replaces {text}(hover text) or {text}(hover text)[CLICK_ACTION|click data] with text components
     *
     * @param s raw string
     * @return parsed TextComponent
     */
    public static @NotNull TextComponent parse(String s) {
        return parseAndReplace(s, null);
    }

    /**
     * Replaces {text}(hover text) or {text}(hover text)[CLICK_ACTION|click data] with text components
     *
     * @param s            raw string
     * @param replacements see {@link #replace(String, Map, OfflinePlayer)}
     * @param player       see {@link #replace(String, Map, OfflinePlayer)}
     * @return parsed TextComponent
     */
    public static TextComponent parseAndReplace(String s, @Nullable Map<String, String> replacements, @Nullable OfflinePlayer player) {
        return parseAndReplace(s, str -> replace(str, replacements, player));
    }

    /**
     * Replaces {text}(hover text) or {text}(hover text)[CLICK_ACTION|click data] with text components
     *
     * @param s            raw string
     * @param player       see {@link #replace(String, OfflinePlayer, String...)}
     * @param replacements see {@link #replace(String, OfflinePlayer, String...)}
     * @return parsed TextComponent
     */
    public static TextComponent parseAndReplace(String s, @Nullable OfflinePlayer player, String... replacements) {
        return parseAndReplace(s, str -> replace(str, player, replacements));
    }

    /**
     * Replaces {text}(hover text) or {text}(hover text)[CLICK_ACTION|click data] with text components
     *
     * @param s       raw string
     * @param replace function that replaces strings
     * @return parsed TextComponent
     */
    public static @NotNull TextComponent parseAndReplace(String s, @Nullable Function<String, String> replace) {
        if (replace == null) replace = str -> str;
        var matcher = componentPattern.matcher(s);
        var mainComponent = new TextComponent();
        while (matcher.find()) {
            var match = matcher.group();
            var text = s.split(Pattern.quote(match))[0];
            var textComponent = new TextComponent(replace.apply(text));
            mainComponent.addExtra(textComponent);
            s = s.substring(text.length() + match.length());
            var component = new TextComponent(replace.apply(matcher.group(1)));
            if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(replace.apply(matcher.group(2)))));
            }
            if (matcher.group(3) != null && matcher.group(4) != null) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(matcher.group(3)), replace.apply(matcher.group(4))));
            }
            mainComponent.addExtra(component);
        }
        mainComponent.addExtra(new TextComponent(s));
        return mainComponent;
    }
}
