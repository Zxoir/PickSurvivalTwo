package me.zxoir.picksurvivaltwo.database;

import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.data.Warp;
import me.zxoir.picksurvivaltwo.listeners.PlayerProfileListener;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataFile {
    public FileConfiguration configuration;
    public File configurationFile;

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    public void setup() {
        PickSurvivalTwo mainInstance = PickSurvivalTwo.getPlugin(PickSurvivalTwo.class);
        if (!mainInstance.getDataFolder().exists()) {
            mainInstance.getDataFolder().mkdir();
        }

        File dataFile = new File(mainInstance.getDataFolder() + File.separator + "Data" + File.separator);
        this.configurationFile = new File(dataFile.getPath(), "DataFile.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.mkdirs();
                Logger.info("the Data folder has been created!");
            } catch (SecurityException e) {
                Logger.warn("Could not create the Data folder. Error: {}", e.getMessage());
            }
        }

        if (!this.configurationFile.exists()) {
            try {
                this.configurationFile.createNewFile();
                Logger.info("the DataFile.yml file has been created!");
            } catch (IOException e) {
                Logger.trace("Could not create the DataFile.yml file. Error: {}", e.getMessage());
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);

        if (configuration.contains("whitelistedBackWorlds")) {
            GlobalCache.loadWhitelistedBackWorlds();
        }

        if (configuration.getString("Spawn") != null) {
            Location location = configuration.getLocation("Spawn");
            GlobalCache.setSpawnWarp(new Warp(location));
        }

        if (configuration.getString("FirstJoinKit") != null) {
            List<ItemStack> itemStacks = (List<ItemStack>) configuration.get("FirstJoinKit");
            if (itemStacks != null)
                PlayerProfileListener.setKitItems(itemStacks.toArray(new ItemStack[0]));
        }
    }

    public FileConfiguration getConfig() {
        return this.configuration;
    }

    public void saveConfig() {
        try {
            this.configuration.save(this.configurationFile);
        } catch (IOException localIOException) {
            Logger.trace("Could not save the DataFile.yml file. Error: {}", localIOException.getMessage());
        }
    }

    public void reloadConfig() {
        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
    }
}
