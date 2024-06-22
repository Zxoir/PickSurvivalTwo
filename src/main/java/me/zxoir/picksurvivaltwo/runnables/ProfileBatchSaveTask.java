package me.zxoir.picksurvivaltwo.runnables;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileDatabaseManager;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class ProfileBatchSaveTask extends BukkitRunnable {
    private static final int BATCH_SIZE = 30;

    @Override
    public void run() {
        List<PlayerProfile> profilesToSave = new ArrayList<>();

        for (PlayerProfile profile : PlayerProfileManager.getCachedProfiles().asMap().values()) {
            profilesToSave.add(profile);

            if (profilesToSave.size() >= BATCH_SIZE) {
                PlayerProfileDatabaseManager.batchUpdatePlayerProfiles(profilesToSave);
                profilesToSave.clear();
            }
        }

        if (!profilesToSave.isEmpty())
            PlayerProfileDatabaseManager.batchUpdatePlayerProfiles(profilesToSave);
    }
}
