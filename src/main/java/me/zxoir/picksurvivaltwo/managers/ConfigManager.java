package me.zxoir.picksurvivaltwo.managers;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final PickSurvivalTwo main = PickSurvivalTwo.getPlugin(PickSurvivalTwo.class);

    @Getter
    private static String username;

    @Getter
    private static String database;

    @Getter
    private static String password;

    @Getter
    private static String ip;

    @Getter
    private static int port;

    @Getter
    private static boolean allowEndPortal;

    private static String joinMessage;

    private static String quitMessage;

    @Getter
    private static String primaryColor;

    @Getter
    private static String secondaryColor;

    @Getter
    private static String errorColor;

    @Getter
    private static boolean EnableScoreboard;

    private static String scoreboardName;

    @Getter
    private static int scoreboardRefresh;

    private static List<String> scoreboard;

    @Getter
    private static boolean bedrockEnableScoreboard;

    private static String bedrockScoreboardName;

    @Getter
    private static int bedrockScoreboardRefresh;

    private static List<String> bedrockScoreboard;


    private static void getConfigData() {
        username = main.getConfig().getString("username");
        database = main.getConfig().getString("database");
        password = main.getConfig().getString("password");
        ip = main.getConfig().getString("ip");
        port = main.getConfig().getInt("port");
        allowEndPortal = main.getConfig().getBoolean("AllowEndPortal");
        primaryColor = main.getConfig().getString("PrimaryColor");
        secondaryColor = main.getConfig().getString("SecondaryColor");
        errorColor = main.getConfig().getString("ErrorColor");
        joinMessage = replaceCommonPlaceholders(main.getConfig().getString("JoinMessage"));
        quitMessage = replaceCommonPlaceholders(main.getConfig().getString("QuitMessage"));
        EnableScoreboard = main.getConfig().getBoolean("Enable-Scoreboard");
        scoreboardName = Colors.colorize(main.getConfig().getString("ScoreboardName"));
        scoreboardRefresh = main.getConfig().getInt("ScoreboardRefresh");
        scoreboard = main.getConfig().getStringList("Scoreboard");
        bedrockEnableScoreboard = main.getConfig().getBoolean("Bedrock-Enable-Scoreboard");
        bedrockScoreboardName = Colors.colorize(main.getConfig().getString("Bedrock-ScoreboardName"));
        bedrockScoreboardRefresh = main.getConfig().getInt("Bedrock-ScoreboardRefresh");
        bedrockScoreboard = main.getConfig().getStringList("Bedrock-Scoreboard");
    }

    public static void setup() {
        main.saveDefaultConfig();
        getConfigData();
    }

    public static void reloadConfig() {
        main.reloadConfig();
        PickSurvivalTwo.getDataFile().reloadConfig();
        getConfigData();
        ScoreboardManager.setHasCachedBoards(false);
    }

    public static @NotNull String getJoinMessage(String playerName) {
        return joinMessage.replace("%player_name%", playerName);
    }

    public static @NotNull String getQuitMessage(String playerName) {
        return quitMessage.replace("%player_name%", playerName);
    }

    public static @NotNull String getScoreboardName() {
        return replaceCommonPlaceholders(scoreboardName);
    }

    public static @NotNull List<String> getScoreboard() {
        List<String> finalScoreboard = new ArrayList<>();
        scoreboard.forEach(line -> finalScoreboard.add(replaceCommonPlaceholders(line)));
        return finalScoreboard;
    }

    public static @NotNull String getBedrockScoreboardName() {
        return replaceCommonPlaceholders(bedrockScoreboardName);
    }

    public static @NotNull List<String> getBedrockScoreboard() {
        List<String> finalScoreboard = new ArrayList<>();
        bedrockScoreboard.forEach(line -> finalScoreboard.add(replaceCommonPlaceholders(line)));
        return finalScoreboard;
    }

    private static @NotNull String replaceCommonPlaceholders(String message) {
        if (message == null)
            return "";

        return Colors.colorize(message).replace("%primary_color%", Colors.primary).replace("%secondary_color%", Colors.secondary).replace("%error_color%", Colors.error);
    }
}
