package me.zxoir.picksurvivaltwo.data;

import lombok.Getter;
import me.zxoir.picksurvivaltwo.managers.PlayerProfileDatabaseManager;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2024 Zxoir
 *
 * @author Zxoir
 * @since 6/17/2024
 */

@Getter
public class Stats {
    private final UUID uuid;
    private int deaths;
    private int playersReferred;
    private boolean referred;
    private Duration playtime;
    private Instant playtimeSession;

    public Stats(UUID uuid) {
        this(uuid, 0, 0, false, Duration.ZERO);
    }

    public Stats(UUID uuid, int deaths, int playersReferred, boolean referred, Duration playtime) {
        this.uuid = uuid;
        this.deaths = deaths;
        this.playersReferred = playersReferred;
        this.referred = referred;
        this.playtime = playtime;
        this.playtimeSession = null;
    }

    public Duration getCurrentPlaytime() {
        if (playtimeSession == null)
            return playtime;

        Duration sessionDuration = Duration.between(playtimeSession, Instant.now());
        return playtime.plus(sessionDuration);
    }

    public synchronized void setPlaytimeSession(Instant playtimeSession) {
        if (playtimeSession == null && this.playtimeSession != null) {
            Duration sessionDuration = Duration.between(this.playtimeSession, Instant.now());
            playtime = playtime.plus(sessionDuration);
        }

        this.playtimeSession = playtimeSession;
    }

    public synchronized void setPlayersReferred(int playersReferred) {
        this.playersReferred = playersReferred;
        PlayerProfileDatabaseManager.updatePlayersReferred(uuid, playersReferred);
    }

    public synchronized void updatePlayersReferred(int delta) {
        this.playersReferred += delta;
        PlayerProfileDatabaseManager.updatePlayersReferred(uuid, playersReferred);
    }

    public synchronized void setReferred(boolean referred) {
        this.referred = referred;
        PlayerProfileDatabaseManager.updateReferred(uuid, referred);
    }

    public synchronized void setDeaths(int deaths) {
        this.deaths = deaths;
        PlayerProfileDatabaseManager.updateDeaths(uuid, deaths);
    }

    public synchronized void updateDeaths(int delta) {
        this.deaths += delta;
        PlayerProfileDatabaseManager.updateDeaths(uuid, deaths);
    }

    public synchronized void setPlaytime(Duration playtime) {
        this.playtime = playtime;
    }
}
