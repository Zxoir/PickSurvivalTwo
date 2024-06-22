package me.zxoir.picksurvivaltwo.commands;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import me.zxoir.picksurvivaltwo.util.CommonUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/23/2023
 */
public class CheckpointCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;
        Player player = (Player) sender;
        PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {

                if (profile.getCheckpoints().containsKey(args[1].toLowerCase())) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&cA checkpoint with this name already exists!")));
                    return true;
                }

                profile.addCheckpoint(args[1].toLowerCase(), player.getLocation());
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&aCheckpoint created!")));
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!profile.getCheckpoints().containsKey(args[1].toLowerCase())) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&cThis checkpoint doesn't exist.")));
                    return true;
                }

                profile.removeCheckpoint(args[1].toLowerCase());
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&aCheckpoint removed.")));
                return true;
            }

            if (args[0].equalsIgnoreCase("track")) {

                if (!profile.getCheckpoints().containsKey(args[1].toLowerCase())) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&cThat checkpoint doesn't exist!")));
                    return true;
                }

                if (profile.getCache().getTrackLocation() != null) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&cYou are already tracking something.")));
                    return true;
                }

                Location checkpoint = profile.getCheckpoints().get(args[1].toLowerCase());

                if (checkpoint == null || checkpoint.getWorld() == null) {
                    player.sendMessage(colorize("&cCheckpoint location has been corrupted. Please try creating another one!"));
                    return true;
                }

                if (!checkpoint.getWorld().getName().equals(player.getWorld().getName())) {
                    player.sendMessage(colorize("&cYou must be in the same world as the Checkpoint"));
                    return true;
                }

                profile.getCache().trackCheckpoint(checkpoint);
                return true;
            }

        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("untrack")) {
                if (profile.getCache().getTrackLocation() == null) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&cYou're not tracking a checkpoint.")));
                    return true;
                }

                profile.getCache().untrackCheckpoint();
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&aYou are no longer tracking a checkpoint")));
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (profile.getCheckpoints().isEmpty()) {
                    player.sendMessage(colorize("&cYou have no checkpoints =c"));
                    return true;
                }

                Component component = LegacyComponentSerializer.legacySection().deserialize(colorize("\n&e&lHere are a list of your checkpoints:\n"));
                for (String checkpoint : profile.getCheckpoints().keySet()) {
                    Location checkpointLocation = profile.getCheckpoints().get(checkpoint);
                    String loc = String.format("%.2f", checkpointLocation.getX()) + ", " + checkpointLocation.getY() + ", " + String.format("%.2f", checkpointLocation.getZ());
                    Component point = LegacyComponentSerializer.legacySection().deserialize(colorize("&8• &c" + checkpoint + " &7- &b(Click to track)\n"))
                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/checkpoint track " + checkpoint))
                            .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(colorize("&bClick to track " + checkpoint + "\n&8Location: &7" + loc))));
                    component = component.append(point);
                }

                player.sendMessage(component);
                return true;
            }
        }

        player.sendMessage(colorize(CommonUtils.centerMessage("\n&e&lCheckpoint Commands:") + "\n&a\n&8• &e/Checkpoint Create &c(Name) &7- &bCreate a new checkpoint\n&8• &e/Checkpoint Delete &c(Name) &7- &bDelete a checkpoint\n&8• &e/Checkpoint Track &c(Name) &7- &bTrack a checkpoint\n&8• &e/Checkpoint Untrack &7- &bUntrack a checkpoint\n&8• &e/Checkpoint list &7- &bList of all your checkpoints\n"));
        return true;
    }

}
