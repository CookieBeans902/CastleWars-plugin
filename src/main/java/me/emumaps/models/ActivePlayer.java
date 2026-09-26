package me.emumaps.models;

import java.util.UUID;

public class ActivePlayer {
    public UUID uuid;
    public String teamColor;
    public int kills;
    public int deaths;
    public int captures;
    public int steals;

    public ActivePlayer(UUID uuid, String teamColor) {
        this.uuid = uuid;
        this.teamColor = teamColor;
    }
}
