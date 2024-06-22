package me.zxoir.picksurvivaltwo.data;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/18/2024
 */

@Getter
public class Cache {
    private final UUID uuid;
    private Location trackLocation;
    private BukkitTask trackingTask;

    @Setter
    private Location backLocation;

    public Cache(UUID uuid) {
        this.uuid = uuid;
        this.backLocation = null;
    }

    public void untrackCheckpoint() {
        trackLocation = null;
        if (trackingTask == null || trackingTask.isCancelled())
            return;

        trackingTask.cancel();
        trackingTask = null;
    }

    public void trackCheckpoint(Location location) {
        if (trackLocation != null)
            return;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline() || !player.getLocation().getWorld().getName().equals(location.getWorld().getName())) {
            return;
        }

        trackLocation = location;
        trackingTask = new BukkitRunnable() {
            int TIMEOUT_TIME = 60 * 60;

            @Override
            public void run() {
                Location currentLocation = player.getLocation();
                double distance = player.getLocation().distance(trackLocation);
                String direction = calculateDirection(currentLocation, trackLocation);

                Component message = distance >= 1 ? LegacyComponentSerializer.legacySection().deserialize(colorize("&aYou are " + String.format("%.2f", distance) + " blocks away! &8(&b" + direction + "&8)")) : LegacyComponentSerializer.legacySection().deserialize(colorize("&aYou have arrived!"));
                player.sendActionBar(message);

                if (distance < 1) {
                    untrackCheckpoint();
                }

                if (TIMEOUT_TIME-- <= 0) {
                    untrackCheckpoint();
                }
            }
        }.runTaskTimerAsynchronously(PickSurvivalTwo.getPlugin(PickSurvivalTwo.class), 0, 20);
    }

    private @NotNull String calculateDirection(@NotNull Location from, @NotNull Location to) {
        Vector fromTo = to.clone().subtract(from).toVector();
        Vector fromLooking = from.getDirection();
        double angle = Math.atan2(fromTo.getX() * fromLooking.getZ() - fromTo.getZ() * fromLooking.getX(), fromTo.getX() * fromLooking.getX() + fromTo.getZ() * fromLooking.getZ()) * 180 / Math.PI;
        if (angle >= -45 && angle < 45) {
            return "↑";
        } else if (angle >= 45 && angle < 135) {
            return "←";
        } else if (angle >= 135 && angle <= 180 || angle >= -180 && angle < -135) {
            return "↓";
        } else if (angle >= -135 && angle < -45) {
            return "→";
        }
        return "";
    }

}
