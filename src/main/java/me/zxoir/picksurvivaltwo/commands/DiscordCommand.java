package me.zxoir.picksurvivaltwo.commands;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 1/19/2022
 */
public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        TextComponent component = LegacyComponentSerializer.legacySection().deserialize(colorize("\n#7289DA&lDiscord: ")).hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(colorize("&aClick here"))));
        TextComponent link = LegacyComponentSerializer.legacySection().deserialize(colorize("&ehttps://discord.gg/gtmwUfX7PT\n")).clickEvent(ClickEvent.openUrl("https://discord.gg/gtmwUfX7PT"));
        player.sendMessage(component.append(link));
        return true;
    }
}