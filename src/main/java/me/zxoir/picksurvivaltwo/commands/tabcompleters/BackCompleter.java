package me.zxoir.picksurvivaltwo.commands.tabcompleters;

import me.zxoir.picksurvivaltwo.util.CommonUtils;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class BackCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();

        if (!(sender instanceof Player player))
            return completer;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION))
            return new ArrayList<>();

        if (args.length == 1) {
            completer.add("whitelist");
            return CommonUtils.smartComplete(args, completer);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            completer.add("Add");
            completer.add("Remove");

            return CommonUtils.smartComplete(args, completer);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("whitelist")) {
            for (World world : Bukkit.getWorlds()) {
                completer.add(world.getName());
            }

            return CommonUtils.smartComplete(args, completer);
        }

        return completer;
    }
}