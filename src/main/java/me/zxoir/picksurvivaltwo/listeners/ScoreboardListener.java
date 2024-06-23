package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.events.PlayerAFKEvent;
import me.zxoir.picksurvivaltwo.managers.ConfigManager;
import me.zxoir.picksurvivaltwo.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/20/2024
 */
public class ScoreboardListener implements Listener {
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!ConfigManager.isEnableScoreboard())
            return;

        Bukkit.getScheduler().runTaskLaterAsynchronously(PickSurvivalTwo.getPlugin(PickSurvivalTwo.class), () -> ScoreboardManager.showScoreboard(player, true), 5L);
    }

    @EventHandler
    public void onAfk(@NotNull PlayerAFKEvent event) {
        Player player = event.getPlayer();

        if (!ConfigManager.isEnableScoreboard())
            return;

        ScoreboardManager.showScoreboard(player, !event.isAFK());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!ConfigManager.isEnableScoreboard())
            return;

        ScoreboardManager.showScoreboard(player, false);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (!ConfigManager.isEnableScoreboard())
            return;

        ScoreboardManager.showScoreboard(player, false);
    }
}
