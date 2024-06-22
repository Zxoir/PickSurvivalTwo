package me.zxoir.picksurvivaltwo.util;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.data.Warp;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */

public class GlobalCache {
    public static final String STAFFPERMISSION = "staff.admin";
    public static final String NOPERMISSION = Colors.error + "You don't have permission to use this command";
    @Getter
    private static final HashSet<Player> warpingPlayers = new HashSet<>();
    @Getter
    @Setter
    private static List<World> whitelistedBackWorlds = new ArrayList<>();
    @Setter
    @Getter
    private static Warp spawnWarp;

    public static void saveWhitelistedBackWorlds() {
        List<String> worldNames = whitelistedBackWorlds.stream()
                .map(World::getName)
                .collect(Collectors.toList());

        PickSurvivalTwo.getDataFile().getConfig().set("whitelistedBackWorlds", worldNames);
        PickSurvivalTwo.getDataFile().saveConfig();
    }

    public static void loadWhitelistedBackWorlds() {
        List<String> worldNames = PickSurvivalTwo.getDataFile().getConfig().getStringList("whitelistedBackWorlds");
        if (worldNames.isEmpty())
            return;
        for (String worldString : worldNames) {
            MVWorldManager worldManager = PickSurvivalTwo.getMultiverseCore().getMVWorldManager();
            World world;
            if (worldManager.isMVWorld(worldString))
                world = worldManager.getMVWorld(worldString) == null ? null : worldManager.getMVWorld(worldString).getCBWorld();
            else
                world = Bukkit.getWorld(worldString);
            whitelistedBackWorlds.add(world);
        }
    }
}