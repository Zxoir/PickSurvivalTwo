package me.zxoir.picksurvivaltwo.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.zxoir.picksurvivaltwo.data.PlayerProfile;
import me.zxoir.picksurvivaltwo.data.Stats;
import me.zxoir.picksurvivaltwo.database.PickDatabase;
import me.zxoir.picksurvivaltwo.util.CommonUtils;
import me.zxoir.picksurvivaltwo.util.LocationAdapter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 4/18/2024
 */
@SuppressWarnings("UnusedReturnValue")
public class PlayerProfileDatabaseManager {
    public static final String TABLE_NAME = "picksurvivalprofiles";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();
    private static final Type CHECKPOINTS_TYPE = new TypeToken<HashMap<String, Location>>() {
    }.getType();

    public static @NotNull ConcurrentLinkedQueue<PlayerProfile> getPlayerProfiles() {
        ConcurrentLinkedQueue<PlayerProfile> users = new ConcurrentLinkedQueue<>();
        long start = System.currentTimeMillis();

        try {
            PickDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s", TABLE_NAME));
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    users.add(resultSetToPlayerProfile(resultSet));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: PPDM_GPP's.01");
            } else {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: PPDM_GPP's.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Logger.debug("Fetched Player Profiles from DB in {} seconds.", finish);

        return users;
    }

    public static @NotNull CompletableFuture<Void> batchUpdatePlayerProfiles(List<PlayerProfile> profiles) {
        return CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();

            try (Connection conn = PickDatabase.getDataSource().getConnection()) {
                PreparedStatement statement = conn.prepareStatement(String.format(
                        "UPDATE %s SET checkpoints = ?, deaths = ?, playersReferred = ?, referred = ?, playtime = ? WHERE uuid = ?", TABLE_NAME));

                for (PlayerProfile profile : profiles) {
                    statement.setString(1, GSON.toJson(profile.getCheckpoints(), CHECKPOINTS_TYPE));
                    statement.setInt(2, profile.getStats().getDeaths());
                    statement.setInt(4, profile.getStats().getPlayersReferred());
                    statement.setBoolean(5, profile.getStats().isReferred());
                    statement.setString(3, profile.getStats().getCurrentPlaytime().toString());
                    statement.setString(6, profile.getUuid().toString());

                    statement.addBatch();
                }

                statement.executeBatch();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Batch updated player profiles to DB in {} seconds.", finish);
            } catch (SQLException e) {
                throw new IllegalStateException("Error while batch updating player profiles in the database", e);
            }
        });
    }

    public static @NotNull PlayerProfile getPlayerProfile(@NotNull UUID uuid) {

        AtomicReference<PlayerProfile> user = new AtomicReference<>(null);
        long start = System.currentTimeMillis();

        try {
            PickDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ? LIMIT 1", TABLE_NAME));
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                user.set(resultSetToPlayerProfile(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: PPDM_GPP.01");
            } else {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: PPDM_GPP.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        PlayerProfile playerProfile = user.get();
        Logger.debug("Fetched Player Profile ('{}') from DB in {} seconds.", playerProfile.getUuid(), finish);

        return playerProfile;
    }

    public static @NotNull CompletableFuture<Void> savePlayerProfile(@NotNull PlayerProfile user) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("INSERT INTO %s VALUES(?, ?, ?, ?, ?, ?, ?)", TABLE_NAME));
            statement.setString(1, user.getUuid().toString());
            statement.setString(2, CommonUtils.getDateFormat().format(user.getJoinDate()));
            statement.setString(3, GSON.toJson(user.getCheckpoints(), CHECKPOINTS_TYPE));
            statement.setInt(4, user.getStats().getDeaths());
            statement.setInt(5, user.getStats().getPlayersReferred());
            statement.setBoolean(6, user.getStats().isReferred());
            statement.setString(7, user.getStats().getCurrentPlaytime().toString());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Saved Player Profile ('{}') to DB in {} seconds.", user.getUuid().toString(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updatePlayerProfile(@NotNull PlayerProfile profile) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format(
                    "UPDATE %s SET checkpoints = ?, deaths = ?, playersReferred = ?, referred = ?, playtime = ? WHERE uuid = ?", TABLE_NAME));

            statement.setString(1, GSON.toJson(profile.getCheckpoints(), CHECKPOINTS_TYPE));
            statement.setInt(2, profile.getStats().getDeaths());
            statement.setInt(3, profile.getStats().getPlayersReferred());
            statement.setBoolean(4, profile.getStats().isReferred());
            statement.setString(5, profile.getStats().getCurrentPlaytime().toString());
            statement.setString(6, profile.getUuid().toString());

            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Updated Player Profile ('{}') from DB in {} seconds.", profile.getUuid(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updatePlaytime(UUID uuid, Duration playtime) {
        return PickDatabase.execute(conn -> {
            try {
                long start = System.currentTimeMillis();

                PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET playtime = ? WHERE uuid = ?", TABLE_NAME));
                statement.setString(1, playtime.toString());
                statement.setString(2, uuid.toString());
                statement.executeUpdate();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Updated Playtime in Player Profile ('{}') from DB in {} seconds.", uuid.toString(), finish);
            } catch (SQLException e) {
                throw new IllegalStateException(String.format("ERROR: Failed to update playtime for user '%s' in the database.", uuid.toString()), e);
            }
        });
    }

    public static @NotNull CompletableFuture<Void> updateDeaths(UUID uuid, int deaths) {
        return PickDatabase.execute(conn -> {
            try {
                long start = System.currentTimeMillis();

                PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET deaths = ? WHERE uuid = ?", TABLE_NAME));
                statement.setInt(1, deaths);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Updated Deaths in Player Profile ('{}') from DB in {} seconds.", uuid.toString(), finish);
            } catch (SQLException e) {
                throw new IllegalStateException(String.format("ERROR: Failed to update deaths for user '%s' in the database.", uuid.toString()), e);
            }
        });
    }

    public static @NotNull CompletableFuture<Void> updateReferred(UUID uuid, boolean referred) {
        return PickDatabase.execute(conn -> {
            try {
                long start = System.currentTimeMillis();

                PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET referred = ? WHERE uuid = ?", TABLE_NAME));
                statement.setBoolean(1, referred);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Updated Referred in Player Profile ('{}') from DB in {} seconds.", uuid.toString(), finish);
            } catch (SQLException e) {
                throw new IllegalStateException(String.format("ERROR: Failed to update referred for user '%s' in the database.", uuid.toString()), e);
            }
        });
    }

    public static @NotNull CompletableFuture<Void> updatePlayersReferred(UUID uuid, int playersReferred) {
        return PickDatabase.execute(conn -> {
            try {
                long start = System.currentTimeMillis();

                PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET playersReferred = ? WHERE uuid = ?", TABLE_NAME));
                statement.setInt(1, playersReferred);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Updated Players Referred in Player Profile ('{}') from DB in {} seconds.", uuid.toString(), finish);
            } catch (SQLException e) {
                throw new IllegalStateException(String.format("ERROR: Failed to update players referred for user '%s' in the database.", uuid.toString()), e);
            }
        });
    }

    public static @NotNull CompletableFuture<Void> updateCheckpoints(UUID uuid, Map<String, Location> checkpoints) {
        return PickDatabase.execute(conn -> {
            try {
                long start = System.currentTimeMillis();

                PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET checkpoints = ? WHERE uuid = ?", TABLE_NAME));
                statement.setString(1, GSON.toJson(checkpoints, CHECKPOINTS_TYPE));
                statement.setString(2, uuid.toString());
                statement.executeUpdate();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Updated Checkpoints in Player Profile ('{}') from DB in {} seconds.", uuid.toString(), finish);
            } catch (SQLException e) {
                throw new IllegalStateException(String.format("ERROR: Failed to update checkpoints for user '%s' in the database.", uuid.toString()), e);
            }
        });
    }

    public static boolean isPlayerInDatabase(UUID uuid) {
        try {
            return PickDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement(String.format("SELECT COUNT(*) FROM %s WHERE uuid = ?", TABLE_NAME));
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("ERROR: Failed to check if player is in Database.", e);
        }
    }

    private static @NotNull PlayerProfile resultSetToPlayerProfile(@NotNull ResultSet resultSet) {
        try {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));

            Instant joinDateInstant = Instant.now();
            try {
                joinDateInstant = CommonUtils.getDateFormat().parse(resultSet.getString("joinDate")).toInstant();
            } catch (ParseException e) {
                Logger.trace("FAILED TO PARSE DATE OF DATA UUID ('{}') WITH ERROR: {}", uuid, e.getMessage());
            }

            Date joinDate = Date.from(joinDateInstant);
            HashMap<String, Location> checkpoints = GSON.fromJson(resultSet.getString("checkpoints"), CHECKPOINTS_TYPE);
            int deaths = resultSet.getInt("deaths");
            int playersReferred = resultSet.getInt("playersReferred");
            boolean referred = resultSet.getBoolean("referred");
            Duration playtime = Duration.parse(resultSet.getString("playtime"));
            Stats stats = new Stats(uuid, deaths, playersReferred, referred, playtime);

            return new PlayerProfile(uuid, joinDate, checkpoints, stats);
        } catch (SQLException e) {
            Logger.trace(e.getMessage());
            throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: PPDM_RSTPP");
        }
    }
}
