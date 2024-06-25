package me.zxoir.picksurvivaltwo.commands;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileManager;
import me.zxoir.picksurvivaltwo.util.Colors;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 8/2/2023
 */
public class ReferCommand implements CommandExecutor {
    @Getter
    final static String REFER_PREFIX = colorize("&6Refer #414438» &r");
    final static int REFER_RANK_GOAL = 5;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player))
            return true;

        if (args.length == 1) {
            PlayerProfile profile = PlayerProfileManager.getPlayerProfile(player.getUniqueId());

            if (args[0].equalsIgnoreCase("count")) {
                player.sendMessage(colorize(REFER_PREFIX + Colors.primary + "You have referred " + Colors.secondary + profile.getStats().getPlayersReferred() + " players"));
            } else {

                if (profile.getStats().isReferred()) {
                    player.sendMessage(colorize(REFER_PREFIX + Colors.error + "You have already been referred by a player."));
                    return true;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
                if (!PlayerProfileManager.isPlayerInDatabase(targetPlayer.getUniqueId())) {
                    player.sendMessage(colorize(REFER_PREFIX + Colors.error + "That player hasn't played on PickMc."));
                    return true;
                }

                if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(colorize(REFER_PREFIX + Colors.error + "You can't refer yourself."));
                    return true;
                }

                PlayerProfile targetProfile = PlayerProfileManager.getPlayerProfile(targetPlayer.getUniqueId());
                targetProfile.getStats().updatePlayersReferred(1);
                profile.getStats().setReferred(true);
                player.sendMessage(colorize(REFER_PREFIX + Colors.primary + "You have been referred by " + Colors.secondary + targetPlayer.getName()));
                boolean isOnline = targetPlayer.isOnline() && targetPlayer.getPlayer() != null;

                if (isOnline)
                    targetPlayer.getPlayer().sendMessage(colorize(REFER_PREFIX + Colors.primary + "Thank you for referring " + Colors.secondary + player.getName() + Colors.primary + " to the server!"));

                if (targetProfile.getStats().getPlayersReferred() == REFER_RANK_GOAL) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + targetPlayer.getName() + " parent add inf");
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(colorize(REFER_PREFIX + Colors.secondary + targetPlayer.getName() + Colors.primary + " has been awarded #AFA9EFInfluencer Rank " + Colors.primary + "for supporting the server!")));

                    if (isOnline) {
                        Title.Times times = Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1));
                        Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("Thank you for supporting " + Colors.primary + "Pick" + Colors.secondary + "Mc")), LegacyComponentSerializer.legacySection().deserialize(colorize("You have been awarded #AFA9EFInfluencer Rank")), times);
                        targetPlayer.getPlayer().showTitle(title);
                        spawnFirework(targetPlayer.getPlayer());
                    }
                }
                return true;
            }

        }

        sender.sendMessage(colorize("""
                #7b634f♦ &6Refer:
                   &eTo refer a player &7— #c7e8e8&l/Refer &b<Player>.
                   &eTo view how many players you have referred &7— #c7e8e8&l/Refer &bcount
                """));

        return true;
    }

    public void spawnFirework(@NotNull Player player) {
        Location location = player.getLocation();

        Firework firework = player.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        // Create a FireworkEffect with the desired properties
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(org.bukkit.Color.RED)
                .withFade(org.bukkit.Color.YELLOW)
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1); // Set the power (1 = single shot)

        firework.setFireworkMeta(fireworkMeta);

        new BukkitRunnable() {
            int fireworks = 5;

            @Override
            public void run() {
                if (fireworks-- <= 0) {
                    cancel();
                    return;
                }

                firework.detonate();
            }
        }.runTaskTimer(PickSurvivalTwo.getPlugin(PickSurvivalTwo.class), 0, 20L);
    }

}
