package me.zxoir.picksurvivaltwo.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zxoir.picksurvivaltwo.managers.AFKManager;
import me.zxoir.picksurvivaltwo.managers.ConfigManager;
import me.zxoir.picksurvivaltwo.util.Colors;
import me.zxoir.picksurvivaltwo.util.CommonUtils;
import me.zxoir.picksurvivaltwo.util.GlobalCache;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/17/2022
 */
public class GeneralListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(LegacyComponentSerializer.legacySection().deserialize(ConfigManager.getJoinMessage(player.getName())));
    }

    @EventHandler
    public void onJoin(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(LegacyComponentSerializer.legacySection().deserialize(ConfigManager.getQuitMessage(player.getName())));
    }

    /* Make Skeleton/Zombie Horses ridable */
    @EventHandler(ignoreCancelled = true)
    public void onInteract(@NotNull PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.SKELETON_HORSE) || event.getRightClicked().getType().equals(EntityType.ZOMBIE_HORSE)) {
            Entity entity = event.getRightClicked();

            entity.addPassenger(event.getPlayer());
        }
    }

    /* Block Parkour Command from any other world */
    @EventHandler(ignoreCancelled = true)
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().substring(1);
        String commandBeginning = command.split(" ")[0];
        PluginCommand pc = Bukkit.getPluginCommand(commandBeginning);

        String pluginName = pc == null ? "NULL" : pc.getPlugin().getName();
        if (!pluginName.equalsIgnoreCase("IP"))
            return;

        if (player.getLocation().getWorld().equals(GlobalCache.getSpawnWarp().getLocation().getWorld()))
            return;

        event.setCancelled(true);
        player.sendMessage(colorize("&cYou can only use this in &lSpawn&c."));
    }

    /* Player Chat Mention */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMention(@NotNull AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        HashSet<Player> mentionedPlayers = new HashSet<>();
        for (String messageSplit : message.split(" ")) {
            Player player = Bukkit.getPlayer(messageSplit);
            if (player != null && player.getName().equalsIgnoreCase(messageSplit)) {
                mentionedPlayers.add(player);
                message = message.replace(messageSplit, player.getName());
            }
        }

        if (mentionedPlayers.isEmpty())
            return;

        for (Player mentioned : mentionedPlayers) {
            mentioned.playSound(mentioned.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

            if (AFKManager.isAFK(mentioned.getUniqueId()))
                CommonUtils.runTaskSync(() -> event.getPlayer().sendMessage(Colors.secondary + mentioned.getName() + NamedTextColor.GRAY + " is AFK and might not respond"));
        }

    }

}
