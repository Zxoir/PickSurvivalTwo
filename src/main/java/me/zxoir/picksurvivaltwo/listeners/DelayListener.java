package me.zxoir.picksurvivaltwo.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zxoir.picksurvivaltwo.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/25/2023
 */
public class DelayListener implements Listener {
    ConcurrentHashMap<UUID, Long> delayedChatPlayers = new ConcurrentHashMap<>();
    long chatDelayInMillisecond = 500;
    ConcurrentHashMap<UUID, Long> delayedCommandPlayers = new ConcurrentHashMap<>();
    long commandDelayInMillisecond = 1000;

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();

        Long previousKey = delayedChatPlayers.putIfAbsent(player.getUniqueId(), System.currentTimeMillis());

        if (previousKey == null || player.isOp())
            return;

        delayedChatPlayers.computeIfPresent(player.getUniqueId(), (key, val) -> {
            long lastChatTime = System.currentTimeMillis() - val;

            if (lastChatTime > chatDelayInMillisecond)
                return null;

            event.setCancelled(true);
            player.sendMessage(colorize(Colors.error + "Please wait to send another message"));
            return val;
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandExecute(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        Long previousKey = delayedCommandPlayers.putIfAbsent(player.getUniqueId(), System.currentTimeMillis());

        if (previousKey == null || player.isOp())
            return;

        delayedCommandPlayers.computeIfPresent(player.getUniqueId(), (key, val) -> {
            long lastCommandTime = System.currentTimeMillis() - val;

            if (lastCommandTime > commandDelayInMillisecond)
                return null;

            event.setCancelled(true);
            player.sendMessage(colorize(Colors.error + "Please wait to use another command"));
            return val;
        });
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        delayedChatPlayers.remove(player.getUniqueId());
        delayedCommandPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        delayedChatPlayers.remove(player.getUniqueId());
        delayedCommandPlayers.remove(player.getUniqueId());
    }
}
