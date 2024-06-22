package me.zxoir.picksurvivaltwo.runnables;

import lombok.AllArgsConstructor;
import me.zxoir.picksurvivaltwo.util.Colors;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */

@AllArgsConstructor
public class WarpRunnable extends BukkitRunnable {
    private int time;
    private Player toBeTeleported;
    private Location location;

    @Override
    public void run() {

        if (toBeTeleported == null || !toBeTeleported.isOnline() || !GlobalCache.getWarpingPlayers().contains(toBeTeleported)) {
            GlobalCache.getWarpingPlayers().remove(toBeTeleported);
            cancel();
            return;
        }

        if (time == 0) {
            toBeTeleported.teleport(location);
            toBeTeleported.sendActionBar(LegacyComponentSerializer.legacySection().deserialize((Colors.primary + "Teleported!")));

            GlobalCache.getWarpingPlayers().remove(toBeTeleported);
            cancel();
            return;
        }

        toBeTeleported.sendMessage(LegacyComponentSerializer.legacySection().deserialize(Colors.primary + "Teleporting you in " + Colors.secondary + time + " second(s)"));
        time--;
    }
}
