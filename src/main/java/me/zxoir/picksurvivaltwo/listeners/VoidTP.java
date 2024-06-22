package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class VoidTP implements Listener {
    List<Player> inTp = new ArrayList<>();

    @EventHandler(ignoreCancelled = true)
    public void voidRandom(@NotNull EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER || event.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;

        Player player = (Player) event.getEntity();
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (!player.getWorld().getName().equals("Spawn") || player.getLocation().getY() > 0)
            return;

        event.setDamage(0);
        player.setFallDistance(0);
        event.setCancelled(true);

        if (inTp.contains(player))
            return;

        if (profile.getCache().getBackLocation() != null) {
            player.teleport(profile.getCache().getBackLocation());
            profile.getCache().setBackLocation(null);
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp player " + player.getName());
        inTp.add(player);

        Bukkit.getScheduler().runTaskLater(PickSurvivalTwo.getPlugin(PickSurvivalTwo.class), () -> inTp.remove(player), 40);
    }
}
