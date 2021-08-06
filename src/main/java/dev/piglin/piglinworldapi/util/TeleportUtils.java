package dev.piglin.piglinworldapi.util;

import dev.piglin.piglinworldapi.PiglinWorldAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class TeleportUtils implements Listener {
    private static final Map<Player, TimedTeleportRecord<?>> timedTeleports = new HashMap<>();

    static {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PiglinWorldAPI.getInstance(), TeleportUtils::checkTeleports, 1, 5);
    }

    /**
     * Wait until {@link TimedTeleportRecord#time} and teleport the player to {@link TimedTeleportRecord#destination} if the player doesn't move
     *
     * @param record The teleport description
     * @return true if teleport was scheduled, false if it wasn't scheduled due to PVP
     */
    public static boolean teleportDelayed(TimedTeleportRecord<?> record, boolean checkPVP) {
        // TODO effects, messages
        if (checkPVP) {
            var isInPVP = PluginsUtils.antiRelogIsInPVP(record.player());
            if (isInPVP != null && isInPVP) {
                return false;
            }
        }
        timedTeleports.put(record.player(), record);
        return true;
    }

    private static void checkTeleports() {
        var toRemove = new ArrayList<Player>();
        var now = System.currentTimeMillis();
        timedTeleports.forEach((player, timedTeleportRecord) -> {
            if (now >= timedTeleportRecord.time()) {
                toRemove.add(player);
                if (timedTeleportRecord.onTeleport().apply((TimedTeleportRecord<Object>) timedTeleportRecord)) {
                    player.teleport(timedTeleportRecord.destination());
                }
            }
        });
        toRemove.forEach(timedTeleports::remove);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (!(event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ())) {
            var record = timedTeleports.remove(event.getPlayer());
            if (record != null) {
                record.onCancel().accept((TimedTeleportRecord<Object>) record);
            }
        }
    }

    /**
     * A timed teleport description
     *
     * @param player      The player
     * @param destination The destination
     * @param time        Timestamp when the player should
     * @param onCancel    Will be executed when the player moves
     *                    Note: Object is always instanceof Meta but I don't know java
     * @param onTeleport  Will be executed when the player should teleport.
     *                    Note: Object is always instanceof Meta but I don't know java
     *                    Returns: true = teleport, false = cancel
     * @param <M>         The additional metadata for your needs
     */
    public record TimedTeleportRecord<M>(@NotNull Player player, @NotNull Location destination, long time,
                                         @NotNull Consumer<TimedTeleportRecord<Object>> onCancel,
                                         @NotNull Function<TimedTeleportRecord<Object>, Boolean> onTeleport,
                                         @Nullable M meta) {
        /**
         * Always teleport player (for onTeleport)
         */
        public static final Function<TimedTeleportRecord<Object>, Boolean> ALWAYS_TELEPORT = o -> true;
        /**
         * Do nothing when cancelled (for onCancel)
         */
        public static final Consumer<TimedTeleportRecord<Object>> NOOP = o -> {

        };
    }
}
