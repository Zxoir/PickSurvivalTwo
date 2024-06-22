package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class WarpListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        GlobalCache.getWarpingPlayers().remove(event.getPlayer());
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        GlobalCache.getWarpingPlayers().remove(event.getPlayer());
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if ((event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()))
            GlobalCache.getWarpingPlayers().remove(player);
    }
}
