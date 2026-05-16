package me.emumaps.models;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ActivePlayer {
    private final UUID playerId;
    private String teamColor;
    private int kills;
    private int deaths;
    private int score;

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }

    public ActivePlayer(@Nonnull UUID playerId, @Nonnull String teamColor) {
        this.playerId = playerId;
        this.teamColor = teamColor;
        this.kills = 0;
        this.deaths = 0;
        this.score = 0;
    }


}

