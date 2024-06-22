package me.zxoir.picksurvivaltwo.util;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/18/2024
 */
public class CommonUtils {
    private static final PickSurvivalTwo mainInstance = PickSurvivalTwo.getPlugin(PickSurvivalTwo.class);
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Getter
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
    @Getter
    private final static int CENTER_PX = 154;

    public static @NotNull String centerMessage(String message) {
        if (message == null || message.isEmpty()) return "";
        else
            message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public static void runTaskSync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTask(mainInstance);
    }

    public static void runTaskAsync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTaskAsynchronously(mainInstance);
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static @NotNull List<String> smartComplete(String @NotNull [] args, @NotNull List<String> list) {
        String arg = args[args.length - 1];
        List<String> temp = new ArrayList<>();

        for (String item : list) {
            if (item.toUpperCase().startsWith(arg.toUpperCase())) {
                temp.add(item);
            }
        }

        return temp;
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean rollPercentage(double percentage) {
        if (percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException("Percentage must be between 0.0 and 100.0");
        }

        // Generate a random number between 0.0 (inclusive) and 100.0 (exclusive)
        double randomNumber = random.nextDouble() * 100.0;

        // Check if the generated random number is less than the specified percentage
        return randomNumber < percentage;
    }

    public interface Task {
        void execute();
    }
}
