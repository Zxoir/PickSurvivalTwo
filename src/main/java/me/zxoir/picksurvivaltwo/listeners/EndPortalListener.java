package me.zxoir.picksurvivaltwo.listeners;

import me.zxoir.picksurvivaltwo.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/25/2023
 */
public class EndPortalListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (ConfigManager.isAllowEndPortal())
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Material clickedBlockType = event.getClickedBlock().getType();

            // Check if the player is trying to create an end portal frame
            if (clickedBlockType == Material.END_PORTAL_FRAME) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("You are not allowed to create an end portal.");
                return;
            }

            // Check if the player is trying to open an end portal with an ender eye
            if (clickedBlockType == Material.END_PORTAL && event.getItem() != null && event.getItem().getType() == Material.ENDER_EYE) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("You are not allowed to open the End Portal.");
            }
        }
    }

}
