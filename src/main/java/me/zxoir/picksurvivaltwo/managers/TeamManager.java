package me.zxoir.picksurvivaltwo.managers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.zxoir.picksurvivaltwo.data.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/17/2024
 */
public class TeamManager {
    @Getter
    private static final Cache<String, Team> cachedTeams = Caffeine.newBuilder()
            .build();
    @Getter
    private static final ConcurrentHashMap<UUID, Team> playerToTeamMap = new ConcurrentHashMap<>();

    public static void createTeam(@NotNull String name, @NotNull UUID uuid) {
        if (cachedTeams.getIfPresent(name.toLowerCase()) != null)
            return;

        Team team = new Team(name, uuid);
        TeamDatabaseManager.saveTeam(team);
        cachedTeams.put(name.toLowerCase(), team);
        playerToTeamMap.put(uuid, team);
    }

    public static Team getTeamByPlayer(UUID playerUuid) {
        return playerToTeamMap.get(playerUuid);
    }

    public static @Nullable Team getTeam(@NotNull String name) {
        return cachedTeams.getIfPresent(name.toLowerCase());
    }

    public static void cacheTeam(@NotNull Team team) {
        if (cachedTeams.getIfPresent(team.getName()) != null)
            return;

        cachedTeams.put(team.getName().toLowerCase(), team);
        playerToTeamMap.put(team.getLeader(), team);
        team.getMembers().forEach(member -> playerToTeamMap.put(member, team));
        Logger.debug("Cached team ('{}')", team.getName());
    }

    public static void removeTeam(@NotNull String name) {
        Team team = cachedTeams.getIfPresent(name.toLowerCase());

        if (team == null)
            return;

        TeamDatabaseManager.deleteTeam(team);
        team.getMembers().forEach(playerToTeamMap::remove);
        cachedTeams.invalidate(name.toLowerCase());
        Logger.debug("Deleted and removed team ('{}') from cache and database", team.getName());
    }
}
