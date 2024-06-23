package me.zxoir.picksurvivaltwo.commands;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.zxoir.picksurvivaltwo.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 8/5/2023
 */
public class ClaimKickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("picksurvivaltwo.claimkick")) {
            player.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Colors.error + "Usage: /claimkick <player>");
            return true;
        }

        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayerExact(playerName);

        if (targetPlayer == null) {
            player.sendMessage(Colors.error + "Player not found or not online.");
            return true;
        }

        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Colors.error + "You cannot kick yourself from your own claim.");
            return true;
        }

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(targetPlayer.getLocation(), false, null);

        if (claim == null) {
            player.sendMessage(Colors.error + "The target player is not standing on a claim.");
            return true;
        }

        UUID ownerId = claim.getOwnerID();
        boolean isTrusted = claim.hasExplicitPermission(targetPlayer.getUniqueId(), ClaimPermission.Build);

        if (!ownerId.equals(player.getUniqueId())) {
            player.sendMessage(Colors.error + "You are not the owner of this claim.");
            return true;
        }

        if (isTrusted || ownerId.equals(targetPlayer.getUniqueId())) {
            player.sendMessage(Colors.error + "You cannot kick a trusted player or the owner from the claim.");
            return true;
        }

        // Teleport the target player outside the claim
        targetPlayer.teleport(claim.getGreaterBoundaryCorner());

        player.sendMessage(Colors.primary + "You have kicked " + Colors.secondary + targetPlayer.getName() + Colors.primary + " off your claim.");
        return true;
    }

}
