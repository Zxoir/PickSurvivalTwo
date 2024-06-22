package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class BackListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (profile.getCache().getBackLocation() != null && event.getTo().equals(profile.getCache().getBackLocation())) {
            profile.getCache().setBackLocation(null);
            return;
        }

        if (!event.getTo().getWorld().equals(GlobalCache.getSpawnWarp().getLocation().getWorld()) || event.getFrom().getWorld().equals(GlobalCache.getSpawnWarp().getLocation().getWorld()))
            return;

        if (event.getFrom().getWorld().getName().equalsIgnoreCase("witp"))
            return;

        if (event.getFrom().getWorld().getName().equalsIgnoreCase("witp") && event.getTo().getWorld().getName().equalsIgnoreCase("witp"))
            return;

        if (!player.isOp())
            profile.getCache().setBackLocation(event.getFrom());
    }

}
