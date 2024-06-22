package me.zxoir.picksurvivaltwo.util;

import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/18/2024
 */
public class UUIDSetSerializer {

    // Serialize Set<UUID> to a comma-separated string
    public static String serialize(@NotNull Set<UUID> set) {
        return set.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));
    }

    // Deserialize comma-separated string to Set<UUID>
    public static @NotNull Set<UUID> deserialize(@NotNull String str) {
        if (str.isEmpty()) {
            return new HashSet<>();
        }

        String[] uuids = str.split(",");
        Set<UUID> set = new HashSet<>();
        for (String uuid : uuids) {
            try {
                set.add(UUID.fromString(uuid));
            } catch (IllegalArgumentException e) {
                Logger.warn("Invalid UUID string: {}", uuid);
            }
        }
        return set;
    }
}