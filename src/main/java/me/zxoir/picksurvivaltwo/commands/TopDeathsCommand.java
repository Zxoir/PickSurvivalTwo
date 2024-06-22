package me.zxoir.picksurvivaltwo.commands;

import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileDatabaseManager;
import me.zxoir.picksurvivaltwo.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/30/2023
 */
public class TopDeathsCommand implements CommandExecutor {
    private LinkedHashMap<String, Integer> topDeathsCache = new LinkedHashMap<>();
    private long lastUpdateTime = 0L;
    private static final long CACHE_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(5);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        long currentTime = System.currentTimeMillis();

        // If the cache is empty or the cache has expired, update the cache
        if (topDeathsCache.isEmpty() || currentTime - lastUpdateTime > CACHE_EXPIRATION_TIME) {
            try {
                updateTopDeathsCache();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            lastUpdateTime = currentTime;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(Colors.colorize("\n&e&lTop 10 players with the most deaths:\n"));

        List<Map.Entry<String, Integer>> topEntries = topDeathsCache.entrySet()
                .stream()
                .limit(10).toList();

        for (int i = 0; i < topEntries.size(); i++) {
            Map.Entry<String, Integer> entry = topEntries.get(i);
            builder.append(Colors.colorize("&8#" + (i + 1) + " &c" + entry.getKey() + " &7- &b" + entry.getValue() + "&a \n&a"));
        }

        sender.sendMessage(builder.toString());
        return true;
    }

    private void updateTopDeathsCache() throws ExecutionException, InterruptedException {
        HashMap<String, Integer> deaths = new HashMap<>();
        Collection<PlayerProfile> dataList = PlayerProfileDatabaseManager.getPlayerProfiles();
        dataList.stream().filter(Objects::nonNull).forEach(data -> deaths.put(Bukkit.getOfflinePlayer(data.getUuid()).getName(), data.getStats().getDeaths()));
        topDeathsCache = sortByValue(deaths);
    }

    private LinkedHashMap<String, Integer> sortByValue(@NotNull HashMap<String, Integer> hm) {
        return hm.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}
