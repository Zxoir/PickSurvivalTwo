package me.zxoir.picksurvivaltwo.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.data.Team;
import me.zxoir.picksurvivaltwo.managers.AFKManager;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import me.zxoir.picksurvivaltwo.managers.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

public class CustomPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "smp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zxoir";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null)
            return null;

        if (params.equalsIgnoreCase("name")) {
            return player.getName();
        }

        if (params.equalsIgnoreCase("team")) {
            Team team = TeamManager.getTeamByPlayer(player.getUniqueId());
            if (team == null || team.getTag() == null || team.getTag().isBlank())
                return "";

            return colorize("&8[ " + team.getColor() + team.getTag() + " &8] &r");
        }

        if (params.contains("_")) {
            String[] param = params.split("_");

            if (param.length < 2)
                return null;

            if (param[0].equalsIgnoreCase("stats")) {
                if (param[1].equalsIgnoreCase("deaths") || param[1].equalsIgnoreCase("death")) {
                    PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
                    return String.valueOf(profile.getStats().getDeaths());
                }

                if (param[1].equalsIgnoreCase("playtime")) {
                    PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());
                    return TimeManager.formatTime(profile.getStats().getCurrentPlaytime().toMillis(), true, true);
                }

                if (param[1].equalsIgnoreCase("afk")) {
                    return AFKManager.isAFK(player.getUniqueId()) ? "true" : "false";
                }
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
