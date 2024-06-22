package me.zxoir.picksurvivaltwo;

import com.onarandombox.MultiverseCore.MultiverseCore;
import lombok.Getter;
import me.zxoir.picksurvivaltwo.commands.*;
import me.zxoir.picksurvivaltwo.commands.tabcompleters.*;
import me.zxoir.picksurvivaltwo.database.DataFile;
import me.zxoir.picksurvivaltwo.database.PickDatabase;
import me.zxoir.picksurvivaltwo.listeners.*;
import me.zxoir.picksurvivaltwo.managers.*;
import me.zxoir.picksurvivaltwo.runnables.ProfileBatchSaveTask;
import me.zxoir.picksurvivaltwo.runnables.TeamBatchSaveTask;
import me.zxoir.picksurvivaltwo.util.CustomPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Objects;

public final class PickSurvivalTwo extends JavaPlugin {
    @Getter
    private static DataFile dataFile;
    @Getter
    private static MultiverseCore multiverseCore;
    private static final int PROFILE_BATCH_SAVE_INTERVAL_MINUTES = 5;
    private static final int TEAM_BATCH_SAVE_INTERVAL_MINUTES = 30;

    @Override
    public void onEnable() {
        Logger.info("Hello world! Initializing Pick Survival Plugin...");
        long initializeTime = System.currentTimeMillis();

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Logger.trace("Couldn't find PlaceholderAPI, shutting down the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {
            Logger.trace("Couldn't find Multiverse-Core, shutting down the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
            Logger.trace("Couldn't find GriefPrevention, shutting down the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ConfigManager.setup();
        multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        dataFile = new DataFile();
        dataFile.setup();

        long processTime = System.currentTimeMillis();
        Logger.debug("Creating Database tables...");
        PickDatabase.createTable("CREATE TABLE IF NOT EXISTS " + PlayerProfileDatabaseManager.TABLE_NAME + "(uuid VARCHAR(36) PRIMARY KEY NOT NULL,joinDate TEXT NOT NULL,checkpoints TEXT,deaths INT,playersReferred INT,referred BOOL,playtime TEXT NOT NULL);");
        PickDatabase.createTable("CREATE TABLE IF NOT EXISTS " + TeamDatabaseManager.TABLE_NAME + "(id VARCHAR(15) PRIMARY KEY NOT NULL,name VARCHAR(15) NOT NULL,leader VARCHAR(36) NOT NULL,members TEXT,creationDate TEXT NOT NULL,color VARCHAR(36) NOT NULL,tag VARCHAR(5));");
        Logger.debug("Database tables created and loaded successfully (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Registering listeners...");
        registerListeners();
        Logger.debug("Registered Listeners Successfully (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Registering commands...");
        registerCommands();
        Logger.debug("Registered Commands Successfully (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Caching all teams...");
        TeamDatabaseManager.getTeams().forEach(TeamManager::cacheTeam);
        Logger.debug("All teams have been cached successfully (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Initializing Batch Profile Saving...");
        new ProfileBatchSaveTask().runTaskTimerAsynchronously(this, 0, 20 * 60 * PROFILE_BATCH_SAVE_INTERVAL_MINUTES);
        Logger.debug("Batch Profile Saving has been initialized (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Initializing Batch Team Saving...");
        new TeamBatchSaveTask().runTaskTimerAsynchronously(this, 0, 20 * 60 * TEAM_BATCH_SAVE_INTERVAL_MINUTES);
        Logger.debug("Batch Team Saving has been initialized (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Initializing AFK Manager...");
        AFKManager.start(this);
        Logger.debug("AFK Manager has been initialized (toke {}ms)", (System.currentTimeMillis() - processTime));

        processTime = System.currentTimeMillis();
        Logger.debug("Registering Placeholders...");
        boolean registeredPlaceholders = new CustomPlaceholders().register();
        Logger.debug("Registered Placeholders Successfully (toke {}ms) {}", (System.currentTimeMillis() - processTime), registeredPlaceholders);

        if (ConfigManager.isEnableScoreboard()) {
            processTime = System.currentTimeMillis();
            Logger.debug("Enabling Scoreboard...");
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> Bukkit.getOnlinePlayers().forEach(ScoreboardManager::updateScoreboard), 0L, ConfigManager.getScoreboardRefresh() * 20L);
            Logger.debug("Scoreboard has been enabled successfully (toke {}ms)", (System.currentTimeMillis() - processTime));
        }

        Logger.info("Pick Survival has been enabled successfully :) (toke {}ms)", (System.currentTimeMillis() - initializeTime));
    }

    @Override
    public void onDisable() {
        Logger.info("Disabling Pick Survival Plugin...");
        long disableTime = System.currentTimeMillis();

        Logger.debug("Saving all player profiles...");
        if (!PlayerProfileManager.getCachedProfiles().asMap().isEmpty())
            PlayerProfileDatabaseManager.batchUpdatePlayerProfiles(new ArrayList<>(PlayerProfileManager.getCachedProfiles().asMap().values()));
        Logger.debug("All player profiles have been saved successfully");

        Logger.debug("Saving all teams...");
        if (!TeamManager.getCachedTeams().asMap().isEmpty())
            TeamDatabaseManager.batchUpdateTeams(new ArrayList<>(TeamManager.getCachedTeams().asMap().values()));
        Logger.debug("All teams have been saved successfully");

        Logger.debug("Closing database connection...");
        PickDatabase.getDataSource().close();
        Logger.debug("Database connection has been closed successfully");

        Logger.debug("Resetting all player titles...");
        getServer().getOnlinePlayers().forEach(Player::resetTitle);
        Logger.debug("All player titles have been reset successfully");

        Logger.info("Pick Survival has been disabled successfully :) (toke {}ms)", (System.currentTimeMillis() - disableTime));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerActivityListener(), this);
        getServer().getPluginManager().registerEvents(new PlaytimeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerProfileListener(), this);
        getServer().getPluginManager().registerEvents(new WarpListener(), this);
        getServer().getPluginManager().registerEvents(new VoidTP(), this);
        getServer().getPluginManager().registerEvents(new StatsListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        getServer().getPluginManager().registerEvents(new EndPortalListener(), this);
        getServer().getPluginManager().registerEvents(new DelayListener(), this);
        getServer().getPluginManager().registerEvents(new BackListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("discord")).setExecutor(new DiscordCommand());
        Objects.requireNonNull(getCommand("team")).setExecutor(new TeamCommand());
        Objects.requireNonNull(getCommand("team")).setTabCompleter(new TeamCompleter());
        Objects.requireNonNull(getCommand("back")).setExecutor(new BackCommand());
        Objects.requireNonNull(getCommand("back")).setTabCompleter(new BackCompleter());
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCommand());
        Objects.requireNonNull(getCommand("spawn")).setTabCompleter(new SpawnCompleter());
        Objects.requireNonNull(getCommand("afk")).setExecutor(new AfkCommand());
        Objects.requireNonNull(getCommand("survival")).setExecutor(new MainCommand());
        Objects.requireNonNull(getCommand("survival")).setTabCompleter(new MainCompleter());
        Objects.requireNonNull(getCommand("checkpoint")).setExecutor(new CheckpointCommand());
        Objects.requireNonNull(getCommand("checkpoint")).setTabCompleter(new CheckpointCompleter());
        Objects.requireNonNull(getCommand("refer")).setExecutor(new ReferCommand());
        Objects.requireNonNull(getCommand("claimkick")).setExecutor(new ClaimKickCommand());
        Objects.requireNonNull(getCommand("topplaytime")).setExecutor(new TopPlaytimeCommand());
        Objects.requireNonNull(getCommand("toprefers")).setExecutor(new TopRefersCommand());
        Objects.requireNonNull(getCommand("topdeaths")).setExecutor(new TopDeathsCommand());
    }
}
