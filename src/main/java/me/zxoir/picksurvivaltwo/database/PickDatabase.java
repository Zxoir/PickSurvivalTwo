package me.zxoir.picksurvivaltwo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import me.zxoir.picksurvivaltwo.managers.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * MIT License Copyright (c) 2023/2024 Zxoir
 *
 * @author Zxoir
 * @since 4/17/2024
 */
public class PickDatabase {
    private static final HikariConfig config = new HikariConfig();
    private static final PickSurvivalTwo mainInstance = PickSurvivalTwo.getPlugin(PickSurvivalTwo.class);
    @Getter
    private static HikariDataSource dataSource;

    public static void createTable(String sqlCreateStatement) {
        Logger.info("Starting DB set up...");
        long start = System.currentTimeMillis();


        if (dataSource == null) {
            FileConfiguration configuration = mainInstance.getConfig();
            String username = ConfigManager.getUsername();
            String database = ConfigManager.getDatabase();
            String password = ConfigManager.getPassword();
            String ip = ConfigManager.getIp();
            int port = ConfigManager.getPort();

            config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database + "?characterEncoding=latin1");
            config.setUsername(username);
            config.setPassword(password);
            config.setConnectionTestQuery("SELECT 1");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
        }

        try {

            execute((connection) -> {
                PreparedStatement statement = connection.prepareStatement(sqlCreateStatement);
                statement.executeUpdate();
            }).get();

        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {

                Logger.error(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: RD_CT.01");
            } else {
                Logger.error(e.getMessage());
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: RD_CT.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Logger.info("Completed DB in {} s", finish);
    }

    public static @NotNull CompletableFuture<Void> execute(ConnectionCallback callback) {
        return CompletableFuture.runAsync(() -> {

            try (Connection conn = dataSource.getConnection()) {
                callback.doInConnection(conn);
            } catch (SQLException e) {
                throw new IllegalStateException("ERROR: Thread was interrupted during execution! Code: RD_E", e);
            }

        });
    }

    public static @NotNull CompletableFuture<Boolean> execute(ConnectionCallbackBoolean callback) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                return callback.doInConnection(conn);
            } catch (SQLException e) {
                throw new IllegalStateException("ERROR: Thread was interrupted during execution! Code: RD_EB", e);
            }
        });
    }

    public interface ConnectionCallbackBoolean {
        boolean doInConnection(Connection conn) throws SQLException;
    }

    public interface ConnectionCallback {
        void doInConnection(Connection conn) throws SQLException;
    }
}
