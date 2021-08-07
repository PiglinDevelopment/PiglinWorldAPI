package dev.piglin.piglinworldapi;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public record ServerConfiguration(int teleportDelay,
                                  @Nullable String teleportMessage,
                                  @Nullable String teleportTitleFormat,
                                  @Nullable String teleportSubtitleFormat,
                                  @Nullable String teleportCancelledMessage,
                                  @Nullable String teleportTeleportedMessage) {
    ServerConfiguration(ConfigurationSection section) {
        this(section.getInt("teleport.delay"),
                section.getString("teleport.message"),
                section.getString("teleport.title format"),
                section.getString("teleport.subtitle format"),
                section.getString("teleport.cancelled message"),
                section.getString("teleport.teleported message"));
    }
}
