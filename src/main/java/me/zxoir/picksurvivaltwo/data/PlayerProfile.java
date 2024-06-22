package me.zxoir.picksurvivaltwo.data;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/16/2024
 */

@Getter
public class PlayerProfile {
    private final UUID uuid;
    private final Date joinDate;
    private final Map<String, Location> checkpoints;
    private final Stats stats;
    private final Cache cache;

    public PlayerProfile(UUID uuid) {
        this(uuid, new Date(), new HashMap<>(), new Stats(uuid));
    }

    public PlayerProfile(UUID uuid, Date joinDate, Map<String, Location> checkpoints, Stats stats) {
        this.uuid = uuid;
        this.joinDate = joinDate;
        this.checkpoints = checkpoints;
        this.stats = stats;
        this.cache = new Cache(uuid);
    }

    public synchronized void addCheckpoint(@NotNull String name, @NotNull Location location) {
        checkpoints.put(name, location);
        PlayerProfileDatabaseManager.updateCheckpoints(uuid, checkpoints);
    }

    public synchronized void removeCheckpoint(@NotNull String name) {
        checkpoints.remove(name);
        PlayerProfileDatabaseManager.updateCheckpoints(uuid, checkpoints);
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
}