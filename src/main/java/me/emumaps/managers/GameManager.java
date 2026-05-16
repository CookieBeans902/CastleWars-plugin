package me.emumaps.managers;

import me.emumaps.models.ActivePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GameManager {
    Map<UUID, ActivePlayer> activePlayers;
    public GameManager() {
        // further implementation would be done.

    }
    public ActivePlayer getPlayer(Player player) {
        return activePlayers.get(player.getUniqueId());
    }
}
