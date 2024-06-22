package me.zxoir.picksurvivaltwo.managers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/17/2024
 */
public class PlayerProfileManager {
    @Getter
    private static final Cache<UUID, PlayerProfile> cachedProfiles = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public static void createPlayerProfile(@NotNull UUID uuid) {
        PlayerProfile profile = new PlayerProfile(uuid);
        PlayerProfileDatabaseManager.savePlayerProfile(profile);
        cacheProfile(profile);
    }

    public static @NotNull PlayerProfile getPlayerProfile(@NotNull UUID uuid) {
        PlayerProfile profile = cachedProfiles.getIfPresent(uuid);

        if (profile == null) {
            profile = PlayerProfileDatabaseManager.getPlayerProfile(uuid);
            cacheProfile(profile);
        }

        return profile;
    }

    public static void cacheProfile(@NotNull PlayerProfile profile) {
        if (cachedProfiles.getIfPresent(profile.getUuid()) != null)
            return;

        cachedProfiles.put(profile.getUuid(), profile);
        Logger.debug("Cached profile ('{}')", profile.getUuid());
    }

    public static boolean isPlayerInDatabase(UUID uuid) {
        if (cachedProfiles.asMap().containsKey(uuid))
            return true;

        return PlayerProfileDatabaseManager.isPlayerInDatabase(uuid);
    }

    public static void removeProfile(UUID uuid) {
        PlayerProfile profile = cachedProfiles.getIfPresent(uuid);

        if (profile == null)
            return;

        PlayerProfileDatabaseManager.updatePlayerProfile(profile);
        cachedProfiles.invalidate(uuid);
        Logger.debug("Removed profile ('{}') from cache", profile.getUuid());
    }
}
