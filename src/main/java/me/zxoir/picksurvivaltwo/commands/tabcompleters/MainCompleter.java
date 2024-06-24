package me.zxoir.picksurvivaltwo.commands.tabcompleters;

import me.zxoir.picksurvivaltwo.util.CommonUtils;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/18/2022
 */
public class MainCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();

        if (!(sender instanceof Player player))
            return completer;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION))
            return completer;

        if (args.length == 1) {
            completer.add("reload");
            completer.add("setFirstJoinKit");
            completer.add("getFirstJoinKit");
            completer.add("stats");

            return CommonUtils.smartComplete(args, completer);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
            completer.add("edit");

            return CommonUtils.smartComplete(args, completer);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("edit")){
            completer.add("deaths");

            return CommonUtils.smartComplete(args, completer);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("deaths")) {
            completer.add("<player>");

            return CommonUtils.smartComplete(args, completer);
        }

        if (args.length == 5 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("deaths")){
            completer.add("<amount>");

            return CommonUtils.smartComplete(args, completer);
        }

        return completer;
    }
}
