package me.zxoir.picksurvivaltwo.managers;

import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zxoir.picksurvivaltwo.util.CommonUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/20/2024
 */
public class ScoreboardManager {
    private static final Set<UUID> playerScoreboards = ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<Integer, Integer> javaBoardCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Integer> bedrockBoardCache = new ConcurrentHashMap<>();

    @Setter
    private static boolean hasCachedBoards = false;

    private static void initScoreboard(@NotNull Player player) {
        cacheBoard();

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("PickMc", Criteria.DUMMY, LegacyComponentSerializer.legacySection().deserialize(ConfigManager.getScoreboardName()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        boolean java = !player.getName().startsWith("#");
        int lineCount = java ? ConfigManager.getScoreboard().size() : ConfigManager.getBedrockScoreboard().size();
        for (String line : java ? ConfigManager.getScoreboard() : ConfigManager.getBedrockScoreboard()) {
            String processedLine = PlaceholderAPI.setPlaceholders(player, line);
            Team team = scoreboard.registerNewTeam("line" + lineCount);
            String blankEntry = getSpaces(lineCount);
            team.addEntry(blankEntry);
            team.prefix(LegacyComponentSerializer.legacySection().deserialize(processedLine));
            objective.getScore(blankEntry).setScore(lineCount);
            lineCount--;
        }

        playerScoreboards.add(player.getUniqueId());
        player.setScoreboard(scoreboard);
    }

    private static void cacheBoard() {
        if (hasCachedBoards)
            return;

        int lineCount = ConfigManager.getScoreboard().size();
        for (String line : ConfigManager.getScoreboard()) {
            if (containsPlaceholders(line))
                javaBoardCache.put(lineCount, ConfigManager.getScoreboard().indexOf(line));

            lineCount--;
        }

        int lineCountBedrock = ConfigManager.getBedrockScoreboard().size();
        for (String line : ConfigManager.getBedrockScoreboard()) {
            if (containsPlaceholders(line))
                bedrockBoardCache.put(lineCountBedrock, ConfigManager.getBedrockScoreboard().indexOf(line));

            lineCountBedrock--;
        }

        hasCachedBoards = true;
    }

    public static void showScoreboard(Player player, boolean show) {
        if (show) {
            if (playerScoreboards.contains(player.getUniqueId()))
                return;
            CommonUtils.runTaskSync(() -> initScoreboard(player));
            return;
        }

        removeScoreboard(player);
    }

    private static void removeScoreboard(@NotNull Player player) {
        playerScoreboards.remove(player.getUniqueId());
        CommonUtils.runTaskSync(() -> player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()));
    }

    private static String getSpaces(int lineCount) {
        return " ".repeat(lineCount);
    }

    public static void updateScoreboard(@NotNull Player player) {
        if (!playerScoreboards.contains(player.getUniqueId()))
            return;

        boolean java = !player.getName().startsWith("#");
        Scoreboard scoreboard = player.getScoreboard();
        if (java) {
            for (int line : javaBoardCache.keySet()) {
                String processedLine = PlaceholderAPI.setPlaceholders(player, ConfigManager.getScoreboard().get(javaBoardCache.get(line)));
                Team team = scoreboard.getTeam("line" + line);

                if (team != null)
                    team.prefix(LegacyComponentSerializer.legacySection().deserialize(processedLine));
            }
            return;
        }

        for (int line : bedrockBoardCache.keySet()) {
            String processedLine = PlaceholderAPI.setPlaceholders(player, ConfigManager.getBedrockScoreboard().get(bedrockBoardCache.get(line)));
            Team team = scoreboard.getTeam("line" + line);

            if (team != null)
                team.prefix(LegacyComponentSerializer.legacySection().deserialize(processedLine));
        }
    }

    private static boolean containsPlaceholders(@NotNull String line) {
        String processedLine = line.replace("%secondary_color%", "")
                .replace("%primary_color%", "")
                .replace("%error_color%", "");

        return processedLine.contains("%");
    }
}
