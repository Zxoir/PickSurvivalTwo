package me.zxoir.picksurvivaltwo.runnables;

import me.zxoir.picksurvivaltwo.data.Team;
import me.zxoir.picksurvivaltwo.managers.TeamDatabaseManager;
import me.zxoir.picksurvivaltwo.managers.TeamManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class TeamBatchSaveTask extends BukkitRunnable {
    private static final int BATCH_SIZE = 30;

    @Override
    public void run() {
        List<Team> teamsToSave = new ArrayList<>();

        for (Team team : TeamManager.getCachedTeams().asMap().values()) {
            teamsToSave.add(team);

            if (teamsToSave.size() >= BATCH_SIZE) {
                TeamDatabaseManager.batchUpdateTeams(teamsToSave);
                teamsToSave.clear();
            }
        }

        if (!teamsToSave.isEmpty())
            TeamDatabaseManager.batchUpdateTeams(teamsToSave);
    }
}
