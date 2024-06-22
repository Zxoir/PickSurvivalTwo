package me.zxoir.picksurvivaltwo.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zxoir.picksurvivaltwo.managers.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/19/2024
 */
public class PlayerActivityListener implements Listener {
    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        AFKManager.resetAFKTimer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerCommand(@NotNull PlayerCommandPreprocessEvent event) {
        AFKManager.resetAFKTimer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncChatEvent event) {
        AFKManager.resetAFKTimer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        AFKManager.resetAFKTimer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        AFKManager.addPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        AFKManager.removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(@NotNull PlayerKickEvent event) {
        AFKManager.removePlayer(event.getPlayer().getUniqueId());
    }
}
