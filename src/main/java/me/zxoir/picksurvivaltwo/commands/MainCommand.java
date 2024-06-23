package me.zxoir.picksurvivaltwo.commands;

import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.listeners.PlayerProfileListener;
import me.zxoir.picksurvivaltwo.managers.ConfigManager;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import me.zxoir.picksurvivaltwo.util.Colors;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        boolean isPlayer = sender instanceof Player;

        if (!sender.hasPermission(GlobalCache.STAFFPERMISSION) && isPlayer) {
            sender.sendMessage(GlobalCache.NOPERMISSION);
            return true;
        }

        // make this command that edits a players death stats like /thiscommand stats edit deaths <player> <amount>
        if (args.length == 5 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("deaths")) {
            Player target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                sender.sendMessage(Colors.error + "Player not found");
                return true;
            }
            if (args[4].matches("[0-9]+")) {
                int deaths = Integer.parseInt(args[4]);
                PlayerProfile profile = PlayerProfileManager.getPlayerProfile(target.getUniqueId());
                profile.getStats().setDeaths(deaths);
                sender.sendMessage(Colors.primary + "Deaths set to " + deaths + " for " + target.getName());
            } else
                sender.sendMessage(Colors.error + "Please enter a valid number");
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("reload")) {
                ConfigManager.reloadConfig();
                sender.sendMessage(Colors.primary + "Config reloaded");
                return true;
            }

            // SMP SetFirstJoinKit
            if (args[0].equalsIgnoreCase("setFirstJoinKit")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                PlayerProfileListener.setKitItems(player.getInventory().getContents().clone());
                player.sendMessage(Colors.primary + "First Join Kit has been set as your Inventory");

                PickSurvivalTwo.getDataFile().getConfig().set("FirstJoinKit", PlayerProfileListener.getKitItems());
                PickSurvivalTwo.getDataFile().saveConfig();
                return true;
            }

            if (args[0].equalsIgnoreCase("getFirstJoinKit")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                if (PlayerProfileListener.getKitItems() == null) {
                    player.sendMessage(Colors.error + "There isn't a set First Join Kit");
                    return true;
                }

                player.getInventory().setContents(PlayerProfileListener.getKitItems());
                player.sendMessage(Colors.primary + "First Join Kit given");
                return true;
            }

        }

        sender.sendMessage(colorize(Colors.primary + "\n#E5FF65SMP &lList of Commands\n" + Colors.primary + "/SMP setFirstJoinKit " + Colors.secondary + "Set First Join Kit\n" + Colors.primary + "/SMP getFirstJoinKit " + Colors.secondary + "Get First Join Kit"));
        return true;
    }
}