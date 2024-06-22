package me.zxoir.picksurvivaltwo.data;

import lombok.Data;
import me.zxoir.picksurvivaltwo.commands.TeamCommand;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import me.zxoir.picksurvivaltwo.managers.TeamDatabaseManager;
import me.zxoir.picksurvivaltwo.managers.TeamManager;
import me.zxoir.picksurvivaltwo.util.Colors;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/19/2024
 */
@Data
public class Team {
    String name;
    String tag;
    String color;
    UUID leader;
    Set<UUID> members;
    Date creationDate;

    public Team(String name, String tag, String color, UUID leader, Set<UUID> members, Date creationDate) {
        this.name = name;
        this.tag = tag;
        this.color = color;
        this.leader = leader;
        this.members = members;
        this.creationDate = creationDate;
    }

    public Team(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.color = "&f";
        this.creationDate = new Date();

        members = new HashSet<>();
    }

    public synchronized void disband() {
        TeamManager.removeTeam(name);
        TeamManager.getPlayerToTeamMap().remove(leader);

        if (members.isEmpty())
            return;

        for (UUID uuid : members) {
            PlayerProfile memberProfile = PlayerProfileManager.getPlayerProfile(uuid);
            TeamManager.getPlayerToTeamMap().remove(uuid);

            if (memberProfile.getPlayer() != null)
                memberProfile.getPlayer().sendMessage(TeamCommand.getTEAM_PREFIX() + Colors.primary + "Team " + Colors.secondary + name + Colors.primary + " has been disbanded!");
        }
    }

    public synchronized void addMember(UUID uuid) {
        members.add(uuid);
        TeamManager.getPlayerToTeamMap().put(uuid, this);
        TeamDatabaseManager.updateMembers(this);
    }

    public synchronized void removeMember(UUID uuid) {
        members.remove(uuid);
        TeamManager.getPlayerToTeamMap().remove(uuid);
        TeamDatabaseManager.updateMembers(this);
    }

    public synchronized void setColor(String color) {
        this.color = color;
        TeamDatabaseManager.updateColor(this);
    }

    public synchronized void setLeader(UUID leader) {
        this.leader = leader;
        TeamDatabaseManager.updateLeader(this);
    }

    public synchronized void setTag(String tag) {
        this.tag = tag;
        TeamDatabaseManager.updateTag(this);
    }
}
