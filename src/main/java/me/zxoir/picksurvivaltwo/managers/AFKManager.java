package me.zxoir.picksurvivaltwo.managers;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.events.PlayerAFKEvent;
import me.zxoir.picksurvivaltwo.util.CommonUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/19/2024
 */
public class AFKManager {
    private static final long AFK_THRESHOLD = 300000L; // 5 minutes in milliseconds
    private static final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();
    private static final Title.Times AFK_ENABLE_TIME = Title.Times.times(Duration.ofSeconds(2), Duration.ofDays(1), Duration.ofSeconds(1));
    private static final Title.Times AFK_DISABLE_TIME = Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ofSeconds(2));

    @Getter
    private static final Map<UUID, Boolean> isAFK = new ConcurrentHashMap<>();

    public static synchronized void resetAFKTimer(UUID uuid) {
        lastActivity.put(uuid, System.currentTimeMillis());
        if (isAFK(uuid)) {
            setAFK(uuid, false);
        }
    }

    public static void addPlayer(UUID uuid) {
        lastActivity.put(uuid, System.currentTimeMillis());
        isAFK.put(uuid, false);
    }

    public static void removePlayer(UUID uuid) {
        lastActivity.remove(uuid);
        isAFK.remove(uuid);
    }

    public static boolean isAFK(UUID uuid) {
        return isAFK.getOrDefault(uuid, false);
    }

    private static void afkTitle(Player player, boolean afk) {
        if (afk) {
            Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), AFK_ENABLE_TIME);
            player.showTitle(title);
            return;
        }

        Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), AFK_DISABLE_TIME);
        player.showTitle(title);
    }

    public static synchronized void setAFK(UUID uuid, boolean afk) {
        isAFK.put(uuid, afk);
        Player player = Bukkit.getPlayer(uuid);

        if (player == null)
            return;

        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(uuid);
        PlayerAFKEvent afkEvent = new PlayerAFKEvent(player, afk, profile);
        afkTitle(player, afk);
        CommonUtils.runTaskSync(() -> Bukkit.getServer().getPluginManager().callEvent(afkEvent));
    }

    public static void start(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Map.Entry<UUID, Long> entry : lastActivity.entrySet()) {
                    UUID uuid = entry.getKey();
                    long lastActive = entry.getValue();
                    if (now - lastActive > AFK_THRESHOLD) {
                        if (!isAFK(uuid)) {
                            setAFK(uuid, true);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 100L); // Check every 5 seconds (100 ticks)
    }
}
