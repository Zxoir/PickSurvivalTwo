package me.zxoir.picksurvivaltwo.commands.tabcompleters;

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
public class SpawnCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();

        if (!(sender instanceof Player player))
            return completer;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION))
            return completer;

        if (args.length == 1) {
            completer.add("Set");
            completer.add("SetSpawn");

            return completer;
        }

        return completer;
    }
}
