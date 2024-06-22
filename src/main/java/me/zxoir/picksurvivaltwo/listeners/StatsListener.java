package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class StatsListener implements Listener {

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(event.getEntity().getUniqueId());
        profile.getStats().updateDeaths(1);
    }


}
