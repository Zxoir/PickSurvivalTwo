package me.zxoir.picksurvivaltwo.commands.tabcompleters;

import me.zxoir.picksurvivaltwo.util.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/30/2023
 */
public class TeamCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();

        if (!(sender instanceof Player))
            return completer;

        if (args.length == 1) {
            completer.add("Create");
            completer.add("Invite");
            completer.add("Tag");
            completer.add("Color");
            completer.add("Disband");
            completer.add("Leave");
            completer.add("Kick");

            return CommonUtils.smartComplete(args, completer);
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {
                completer.add("<NAME>");
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("kick")) {
                completer.add("<PLAYER_NAME>");
                Bukkit.getOnlinePlayers().forEach(online -> completer.add(online.getName()));
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("invite")) {
                completer.add("<PLAYER_NAME>");
                Bukkit.getOnlinePlayers().forEach(online -> completer.add(online.getName()));
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("tag")) {
                completer.add("<TAG>");
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("color")) {
                completer.add("<HEX/COLOR>");
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("disband")) {
                completer.add("Confirm");
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("leave")) {
                completer.add("Confirm");
                return CommonUtils.smartComplete(args, completer);
            }

        }

        return completer;
    }
}
