package me.zxoir.picksurvivaltwo.commands;

import me.zxoir.picksurvivaltwo.managers.AFKManager;
import me.zxoir.picksurvivaltwo.util.Colors;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/17/2022
 */
public class AfkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
            player.sendMessage(GlobalCache.NOPERMISSION);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(Colors.error + "That player is not Online");
                return true;
            }

            if (AFKManager.getIsAFK().getOrDefault(target.getUniqueId(), false)) {
                AFKManager.setAFK(target.getUniqueId(), false);
                return true;
            }

            AFKManager.setAFK(target.getUniqueId(), true);
            return true;
        }

        if (AFKManager.isAFK(player.getUniqueId())) {
            AFKManager.setAFK(player.getUniqueId(), false);
            return true;
        }

        AFKManager.setAFK(player.getUniqueId(), true);
        return true;
    }

}