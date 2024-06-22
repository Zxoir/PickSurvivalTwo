package me.zxoir.picksurvivaltwo.managers;

import me.zxoir.picksurvivaltwo.data.Team;
import me.zxoir.picksurvivaltwo.database.PickDatabase;
import me.zxoir.picksurvivaltwo.util.CommonUtils;
import me.zxoir.picksurvivaltwo.util.UUIDSetSerializer;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
public class TeamDatabaseManager {
    public static final String TABLE_NAME = "picksurvivalteams";

    public static @NotNull ConcurrentLinkedQueue<Team> getTeams() {
        ConcurrentLinkedQueue<Team> teams = new ConcurrentLinkedQueue<>();
        long start = System.currentTimeMillis();

        try {
            PickDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s", TABLE_NAME));
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    teams.add(resultSetToTeam(resultSet));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: TDM_GT'S.01");
            } else {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: TDM_GT'S.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Logger.debug("Fetched Teams from DB in {} seconds.", finish);

        return teams;
    }

    public static @NotNull CompletableFuture<Void> batchUpdateTeams(List<Team> teams) {
        return CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();

            try (Connection conn = PickDatabase.getDataSource().getConnection()) {
                PreparedStatement statement = conn.prepareStatement(String.format(
                        "UPDATE %s SET leader = ?, members = ?, color = ?, tag = ? WHERE id = ?", TABLE_NAME));

                for (Team team : teams) {
                    statement.setString(1, team.getLeader().toString());
                    statement.setString(2, UUIDSetSerializer.serialize(team.getMembers()));
                    statement.setString(3, team.getColor());
                    statement.setString(4, team.getTag());
                    statement.setString(5, team.getName());

                    statement.addBatch();
                }

                statement.executeBatch();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Logger.debug("Batch updated teams to DB in {} seconds.", finish);
            } catch (SQLException e) {
                throw new IllegalStateException("Error while batch updating teams in the database", e);
            }
        });
    }

    public static @NotNull Team getTeam(@NotNull String name) {

        AtomicReference<Team> teamReference = new AtomicReference<>(null);
        long start = System.currentTimeMillis();

        try {
            PickDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s WHERE id = ? LIMIT 1", TABLE_NAME));
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                teamReference.set(resultSetToTeam(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: TDM_GT.01");
            } else {
                Logger.trace(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: TDM_GT.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Team team = teamReference.get();
        Logger.debug("Fetched Team ('{}') from DB in {} seconds.", team.getName(), finish);

        return team;
    }

    public static @NotNull CompletableFuture<Void> saveTeam(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("INSERT INTO %s VALUES(?, ?, ?, ?, ?, ?, ?)", TABLE_NAME));
            statement.setString(1, team.getName().toLowerCase());
            statement.setString(2, team.getName());
            statement.setString(3, team.getLeader().toString());
            statement.setString(4, UUIDSetSerializer.serialize(team.getMembers()));
            statement.setString(5, CommonUtils.getDateFormat().format(team.getCreationDate()));
            statement.setString(6, team.getColor());
            statement.setString(7, team.getTag());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Saved Team ('{}') to DB in {} seconds.", team.getName(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> deleteTeam(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("DELETE FROM %s WHERE name = ?", TABLE_NAME));
            statement.setString(1, team.getName());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Deleted Team ('{}') from DB in {} seconds.", team.getName(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updateTeam(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format(
                    "UPDATE %s SET leader = ?, members = ?, color = ?, tag = ? WHERE name = ?", TABLE_NAME));

            statement.setString(1, team.getLeader().toString());
            statement.setString(2, UUIDSetSerializer.serialize(team.getMembers()));
            statement.setString(3, team.getColor());
            statement.setString(4, team.getTag());
            statement.setString(5, team.getName());

            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Updated Team ('{}') from DB in {} seconds.", team.getName(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updateLeader(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET leader = ? WHERE name = ?", TABLE_NAME));
            statement.setString(1, team.getLeader().toString());
            statement.setString(2, team.getName());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Updated Leader of Team ('{}') to DB in {} seconds.", team.getName(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updateMembers(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET members = ? WHERE name = ?", TABLE_NAME));
            statement.setString(1, UUIDSetSerializer.serialize(team.getMembers()));
            statement.setString(2, team.getName());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Updated Members of Team ('{}') to DB in {} seconds.", team.getName(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updateColor(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET color = ? WHERE name = ?", TABLE_NAME));
            statement.setString(1, team.getColor());
            statement.setString(2, team.getName());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Updated Color of Team ('{}') to DB in {} seconds.", team.getName(), finish);
        });
    }

    public static @NotNull CompletableFuture<Void> updateTag(@NotNull Team team) {
        return PickDatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(String.format("UPDATE %s SET tag = ? WHERE name = ?", TABLE_NAME));
            statement.setString(1, team.getTag());
            statement.setString(2, team.getName());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Logger.debug("Updated Tag of Team ('{}') to DB in {} seconds.", team.getName(), finish);
        });
    }

    public static boolean isTeamInDatabase(String name) {
        try {
            return PickDatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement(String.format("SELECT COUNT(*) FROM %s WHERE name = ?", TABLE_NAME));
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("ERROR: Failed to check if team is in Database.", e);
        }
    }

    private static @NotNull Team resultSetToTeam(@NotNull ResultSet resultSet) {
        try {
            String name = resultSet.getString("name");
            UUID leader = UUID.fromString(resultSet.getString("leader"));
            Set<UUID> members = UUIDSetSerializer.deserialize(resultSet.getString("members"));
            Instant creationDateInstant = Instant.now();
            try {
                creationDateInstant = CommonUtils.getDateFormat().parse(resultSet.getString("creationDate")).toInstant();
            } catch (ParseException e) {
                Logger.trace("FAILED TO PARSE DATE OF TEAM DATA ('{}') WITH ERROR: {}", name, e.getMessage());
            }
            Date creationDate = Date.from(creationDateInstant);
            String color = resultSet.getString("color");
            String tag = resultSet.getString("tag");

            return new Team(name, tag, color, leader, members, creationDate);
        } catch (SQLException e) {
            Logger.trace(e.getMessage());
            throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: TDM_RSTT");
        }
    }
}
