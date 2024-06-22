package me.zxoir.picksurvivaltwo.commands.tabcompleters;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
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
public class CheckpointCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();

        if (!(sender instanceof Player player))
            return completer;

        if (args.length == 1) {
            completer.add("Create");
            completer.add("Delete");
            completer.add("Track");
            completer.add("Untrack");
            completer.add("List");

            CommonUtils.smartComplete(args, completer);
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("Create")) {
                completer.add("<NAME>");
                Bukkit.getOnlinePlayers().forEach(online -> completer.add(online.getName()));
                return CommonUtils.smartComplete(args, completer);
            }

            if (args[0].equalsIgnoreCase("Delete") || args[0].equalsIgnoreCase("Track")) {
                completer.add("<NAME>");
                PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
                completer.addAll(profile.getCheckpoints().keySet());
                return CommonUtils.smartComplete(args, completer);
            }

        }

        return completer;
    }
}
