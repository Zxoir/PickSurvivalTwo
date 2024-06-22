package me.zxoir.picksurvivaltwo.commands;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.data.Team;
import me.zxoir.picksurvivaltwo.managers.TeamManager;
import me.zxoir.picksurvivaltwo.util.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class TeamCommand implements CommandExecutor {
    HashMap<UUID, List<String>> teamRequest = new HashMap<>();
    @Getter
    final static String TEAM_PREFIX = colorize("&6Teams #414438» &r");

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player))
            return true;

        if (args.length == 1) {
            Team team = TeamManager.getTeamByPlayer(player.getUniqueId());

            if (team == null) {
                player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                Set<UUID> members = team.getMembers();
                StringBuilder memberList = new StringBuilder();

                memberList.append("\n");
                memberList.append(ChatColor.GOLD).append("Team Name: ").append(ChatColor.WHITE).append(team.getName()).append("\n");
                OfflinePlayer leader = Bukkit.getOfflinePlayer(team.getLeader());
                ChatColor statusLeaderColor = (leader.isOnline()) ? ChatColor.GREEN : ChatColor.RED;
                memberList.append(ChatColor.GOLD).append("Team Leader: ").append(statusLeaderColor).append(leader.getName()).append("\n");

                memberList.append(ChatColor.GOLD).append("Members: ");
                if (!members.isEmpty()) {
                    for (UUID uuid : members) {
                        OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
                        ChatColor statusColor = (member.isOnline()) ? ChatColor.GREEN : ChatColor.RED;
                        memberList.append(statusColor).append(member.getName()).append(ChatColor.WHITE).append(", ");
                    }
                } else
                    memberList.append("None.");
                memberList.append("\n");

                player.sendMessage(memberList.toString());
            }

            if (args[0].equalsIgnoreCase("color")) {
                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You must be the Team Leader to change the team color");
                    return true;
                }

                Random random = ThreadLocalRandom.current();
                String hexColor = String.format("#%06x", random.nextInt(0xffffff + 1));
                team.setColor(hexColor);
                player.sendMessage(TEAM_PREFIX + colorize(hexColor + "This is your new Team Color"));
            }

            if (args[0].equalsIgnoreCase("disband")) {
                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You must be the Team Leader to disband the Team!");
                    return true;
                }

                player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "Use /Team Disband Confirm!");
            }

            if (args[0].equalsIgnoreCase("leave")) {
                if (team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You must disband the team");
                    return true;
                }

                player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "Use /Team Leave Confirm!");
            }

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {
                String teamName = args[1];

                if (TeamManager.getTeam(teamName.toLowerCase()) != null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "There is already a team with that name!");
                    return true;
                }

                if (TeamManager.getTeamByPlayer(player.getUniqueId()) != null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are already in a team!");
                    return true;
                }

                TeamManager.createTeam(teamName, player.getUniqueId());
                player.sendMessage(TEAM_PREFIX + Colors.primary + "Team " + Colors.secondary + teamName + Colors.primary + " has been created!");
            }

            if (args[0].equalsIgnoreCase("invite")) {
                Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You must be the Team Leader to invite other players!");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "This player is offline");
                    return true;
                }

                if (TeamManager.getTeamByPlayer(target.getUniqueId()) != null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "That player is already in a team!");
                    return true;
                }

                List<String> requests = new ArrayList<>();

                if (teamRequest.containsKey(target.getUniqueId()))
                    requests = teamRequest.get(target.getUniqueId());

                requests.add(team.getName().toLowerCase());
                teamRequest.put(target.getUniqueId(), requests);

                Component component = LegacyComponentSerializer.legacySection().deserialize(colorize("&eTeam request received from &9" + team.getName() + " "));
                Component accept = LegacyComponentSerializer.legacySection().deserialize(colorize("&a&l[ACCEPT] ")).clickEvent(ClickEvent.runCommand("/team accept " + team.getName())).hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(colorize("&aClick to accept"))));
                Component deny = LegacyComponentSerializer.legacySection().deserialize(colorize("&c&l[DENY] ")).clickEvent(ClickEvent.runCommand("/team deny " + team.getName())).hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(colorize("&cClick to decline"))));
                component = component.append(accept).append(deny);
                target.sendMessage(component);

                player.sendMessage(TEAM_PREFIX + Colors.primary + "You have invited " + Colors.secondary + target.getName() + Colors.primary + " to join your Team");

                Bukkit.getScheduler().runTaskLater(PickSurvivalTwo.getPlugin(PickSurvivalTwo.class), () -> {

                    if (!teamRequest.containsKey(target.getUniqueId()))
                        return;

                    List<String> requestsLater = teamRequest.get(target.getUniqueId());

                    if (requestsLater.contains(team.getName().toLowerCase())) {
                        requestsLater.remove(team.getName().toLowerCase());
                        if (requestsLater.isEmpty())
                            teamRequest.remove(target.getUniqueId());
                        else
                            teamRequest.put(target.getUniqueId(), requestsLater);

                        if (player.isOnline())
                            player.sendMessage(TEAM_PREFIX + Colors.error + target.getName() + "'s invitation to join your team has expired.");
                        if (target.isOnline())
                            target.sendMessage(TEAM_PREFIX + Colors.error + "Your team invitation from " + team.getName() + " has expired");
                    }
                }, 1200);
            }

            if (args[0].equalsIgnoreCase("kick")) {
                Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You must be the Team Leader to kick other players!");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (team.getMembers().contains(target.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "That player is not in your team!");
                    return true;
                }

                team.removeMember(target.getUniqueId());
                player.sendMessage(TEAM_PREFIX + Colors.primary + "You have kicked " + Colors.secondary + target.getName() + Colors.primary + " from your Team");
            }

            if (args[0].equalsIgnoreCase("tag")) {
                Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You must be the Team Leader to change the team color");
                    return true;
                }

                if (!isValidTag(args[1])) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "Make sure the tag is up to 4 letters long and without symbols");
                    return true;
                }

                team.setTag(args[1]);
                player.sendMessage(TEAM_PREFIX + Colors.primary + "Your team tag has been set! You should be able to see it in chat now :)");
            }

            if (args[0].equalsIgnoreCase("color")) {
                Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You must be the Team Leader to change the team color");
                    return true;
                }

                String input = args[1];

                if (!isValidHexColor(input) && !isValidColorCode(input)) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You must enter a valid hex color/color code!");
                    return true;
                }

                team.setColor(input);
                player.sendMessage(TEAM_PREFIX + colorize(input + "Your new team color has been set."));
            }

            if (args[0].equalsIgnoreCase("accept")) {
                String teamName = args[1].toLowerCase();

                if (!teamRequest.containsKey(player.getUniqueId())) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You don't have any team requests!");
                    return true;
                }

                List<String> requests = teamRequest.get(player.getUniqueId());
                if (requests.isEmpty()) {
                    teamRequest.remove(player.getUniqueId());
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You don't have any team requests!");
                    return true;
                }

                if (!requests.contains(teamName)) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You don't have a invite from this Team!");
                    return true;
                }

                Team team = TeamManager.getTeam(teamName);
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "Team not found?!");
                    return true;
                }

                if (TeamManager.getTeamByPlayer(player.getUniqueId()) != null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are already in a team!");
                    return true;
                }

                requests.remove(team.getName().toLowerCase());
                if (requests.isEmpty())
                    teamRequest.remove(player.getUniqueId());
                else
                    teamRequest.put(player.getUniqueId(), requests);

                team.addMember(player.getUniqueId());
            }

            if (args[0].equalsIgnoreCase("deny")) {
                String teamName = args[1].toLowerCase();

                if (!teamRequest.containsKey(player.getUniqueId())) {
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You don't have any team requests!");
                    return true;
                }

                List<String> requests = teamRequest.get(player.getUniqueId());
                if (requests.isEmpty()) {
                    teamRequest.remove(player.getUniqueId());
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You don't have any team requests!");
                    return true;
                }

                if (!requests.contains(teamName)) {
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You don't have a invite from this Team!");
                    return true;
                }

                requests.remove(teamName);
                if (requests.isEmpty())
                    teamRequest.remove(player.getUniqueId());
                else
                    teamRequest.put(player.getUniqueId(), requests);

                player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.primary + "You have declined the Team request from " + Colors.secondary + teamName);
            }

            if (args[0].equalsIgnoreCase("disband") && args[1].equalsIgnoreCase("confirm")) {
                Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You must be the Team Leader to disband the Team!");
                    return true;
                }

                team.disband();
                player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.primary + "Team has been disbanded");
            }

            if (args[0].equalsIgnoreCase("leave") && args[1].equalsIgnoreCase("confirm")) {
                Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(TEAM_PREFIX + Colors.error + "You are not in a Team!");
                    return true;
                }

                if (team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.error + "You must disband the team");
                    return true;
                }

                team.removeMember(player.getUniqueId());
                player.sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.primary + "You have left the team");
            }

        } else
            sender.sendMessage(Colors.colorize("""
                    #7b634f♦ &6Teams:
                       &eTo create a team &7— #c7e8e8&l/Team &bcreate &c[Name].
                       &eTo invite a player to the team &7— #c7e8e8&l/Team &binvite &c[Playername].
                       &eTo make a tag for the team &7— #c7e8e8&l/Team &btag &c[Tag] &8[2-5 Letters Only].
                       &eTo select a random team color &7— #c7e8e8&l/Team &bcolor.
                       &eTo select a specific team color &7— #c7e8e8&l/Team &bcolor &c[Hex].
                       &eTo challange a team  &7— #c7e8e8&l/Team &bchallange &c[Teamname].
                       &eTo disband the team  &7— #c7e8e8&l/Team &bdisband.
                       &eTo leave the team  &7— #c7e8e8&l/Team &bleave.
                    """));

        return true;
    }

    private boolean isValidHexColor(@NotNull String input) {
        if (!input.startsWith("#"))
            return false;

        return input.matches("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
    }

    private boolean isValidTag(@NotNull String input) {
        return input.matches("^[a-zA-Z]{2,4}$");
    }

    @Contract(pure = true)
    private boolean isValidColorCode(@NotNull String colorCode) {
        return colorCode.matches("^&[0-9a-fA-Fk-oK-OrR]$");
    }
}