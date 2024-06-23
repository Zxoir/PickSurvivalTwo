package me.zxoir.picksurvivaltwo.listeners;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.picksurvivaltwo.events.PlayerAFKEvent;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileDatabaseManager;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/19/2024
 */
public class PlayerProfileListener implements Listener {
    @Getter
    @Setter
    private static ItemStack[] kitItems;

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerProfileDatabaseManager.isPlayerInDatabase(player.getUniqueId()))
            return;

        PlayerProfileManager.createPlayerProfile(player.getUniqueId());

        if (!player.getInventory().isEmpty() || kitItems == null)
            return;

        player.getInventory().setContents(kitItems);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAFK(@NotNull PlayerAFKEvent event) {
        if (event.isAFK()) {
            PlayerProfileManager.removeProfile(event.getPlayer().getUniqueId());
            return;
        }

        PlayerProfileManager.cacheProfile(event.getProfile());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        PlayerProfileManager.removeProfile(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(@NotNull PlayerKickEvent event) {
        PlayerProfileManager.removeProfile(event.getPlayer().getUniqueId());
    }
}