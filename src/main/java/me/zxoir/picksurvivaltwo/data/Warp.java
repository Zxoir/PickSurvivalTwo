package me.zxoir.picksurvivaltwo.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.runnables.WarpRunnable;
import me.zxoir.picksurvivaltwo.util.Colors;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */

@Getter
@AllArgsConstructor
public class Warp {
    private Location location;

    public void teleport(@NotNull Player toBeTeleported) {
        if (GlobalCache.getWarpingPlayers().contains(toBeTeleported))
            return;

        toBeTeleported.teleport(location);
        toBeTeleported.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(Colors.primary + "Teleported!"));
    }

    /**
     * @param time Warp time till player is teleported
     */
    public void teleport(Player toBeTeleported, int time) {
        if (GlobalCache.getWarpingPlayers().contains(toBeTeleported))
            return;

        new WarpRunnable(time, toBeTeleported, location).runTaskTimer(PickSurvivalTwo.getPlugin(PickSurvivalTwo.class), 0, 20);
        GlobalCache.getWarpingPlayers().add(toBeTeleported);
    }

}
