package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.events.PlayerAFKEvent;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.time.Instant;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/19/2024
 */
public class PlaytimeListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        profile.getStats().setPlaytimeSession(Instant.now());
        Logger.debug("Started playtime session for {}", player.getName());
    }

    @EventHandler
    public void onAFK(@NotNull PlayerAFKEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = event.getProfile();

        if (event.isAFK()) {
            profile.getStats().setPlaytimeSession(null);
            Logger.debug("Terminated playtime session for {}", player.getName());
            return;
        }

        profile.getStats().setPlaytimeSession(Instant.now());
        Logger.debug("Started playtime session for {}", player.getName());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        profile.getStats().setPlaytimeSession(null);
        Logger.debug("Terminated playtime session for {}", player.getName());
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
        profile.getStats().setPlaytimeSession(null);
        Logger.debug("Terminated playtime session for {}", player.getName());
    }

}
