package dev.piglin.piglinworldapi.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.jeremi.antirelog.AntiRelog;

import java.util.HashMap;

public class PluginsUtils {
    /**
     * Checks if the player is in PVP (See <a href="https://www.spigotmc.org/resources/antirelog.22434/">AntiRelog on SpigotMC</a>)
     *
     * @return true if the player is in PVP, false if the player is not in PVP, null if AntiRelog is not installed
     */
    public static @Nullable Boolean antiRelogIsInPVP(@NotNull Player player) {
        Validate.notNull(player);
        try {
            boolean shouldBePunished = false;
            Class.forName("pl.jeremi.antirelog.AntiRelog");
            var antirelog = AntiRelog.getPlugin(AntiRelog.class);
            var handleField = AntiRelog.class.getDeclaredField("handledPlayers");
            var bypassField = AntiRelog.class.getDeclaredField("bypassingPlayers");
            handleField.setAccessible(true);
            bypassField.setAccessible(true);
            var handledPlayers = (HashMap<Player, ?>) handleField.get(antirelog);
            var bypassingPlayers = (HashMap<Player, ?>) bypassField.get(antirelog);
            if (!bypassingPlayers.containsKey(player) || !((boolean) bypassingPlayers.get(player))) {
                var handle = handledPlayers.get(player);
                var shouldBePunishedMethod = handle.getClass().getDeclaredMethod("shouldBePunished");
                shouldBePunishedMethod.setAccessible(true);
                shouldBePunished = (boolean) shouldBePunishedMethod.invoke(handle);
                shouldBePunishedMethod.setAccessible(false);
            }
            handleField.setAccessible(false);
            bypassField.setAccessible(false);
            return shouldBePunished;
        } catch (ClassNotFoundException ignored) {
            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the placeholders for the target string using PlaceholderAPI (See <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI on SpigotMC</a>)
     *
     * @param player The player for who to set placeholders
     * @param target The string to set placeholders in
     * @return The string with replaced placeholders or null if PlaceholderAPI is not found
     */
    public static @Nullable String placeholderAPISetPlaceholders(@NotNull OfflinePlayer player, @NotNull String target) {
        Validate.notNull(player);
        Validate.notNull(target);
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return PlaceholderAPI.setPlaceholders(player, target);
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }

    /**
     * Sets the placeholders for the target string using PlaceholderAPI (See <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI on SpigotMC</a>)
     *
     * @param player The player for who to set placeholders
     * @param target The string to set placeholders in
     * @return The string with replaced placeholders or the original string if PlaceholderAPI is not found
     */
    public static @NotNull String placeholderAPITrySetPlaceholders(@NotNull OfflinePlayer player, @NotNull String target) {
        Validate.notNull(player);
        Validate.notNull(target);
        var replaced = placeholderAPISetPlaceholders(player, target);
        return replaced == null ? target : replaced;
    }
}
