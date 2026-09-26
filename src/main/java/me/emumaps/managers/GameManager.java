package me.emumaps.managers;

import me.emumaps.models.ActivePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GameManager {
    HashMap<UUID, ActivePlayer> players;
    ArrayList<String> teamColors;
    Scoreboard scoreboard;

    public GameManager(HashMap<UUID,ActivePlayer> players, ArrayList<String> teamColors) {
        this.players = players;
        this.teamColors = teamColors;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for(String color : teamColors) {
            Team team = scoreboard.registerNewTeam(color);
            team.setAllowFriendlyFire(false);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
            if(color.equalsIgnoreCase("red")) {
                team.displayName(Component.text("Red Team").color(NamedTextColor.RED));
            }
            else {
                team.displayName(Component.text("Blue Team").color(NamedTextColor.BLUE));
            }
        }
        for(ActivePlayer player: players.values()) {
            Player playerEntity = Bukkit.getPlayer(player.uuid);
            if (playerEntity == null) continue;
            playerEntity.setScoreboard(scoreboard);
            scoreboard.getTeam(player.teamColor).addEntity(playerEntity);
            playerEntity.setScoreboard(scoreboard);
        }
        Bukkit.broadcast(Component.text("Initialized!"));
        Objective captureObj = this.scoreboard.registerNewObjective("Score", "dummy", Component.text("Nexus Count"));
        captureObj.setDisplaySlot(DisplaySlot.SIDEBAR);
        captureObj.getScore("Red").setScore(10);
        captureObj.getScore("Blue").setScore(10);
    }
    public void NexusCapture(String teamColor) {
        int curr = scoreboard.getObjective("Score").getScore(teamColor).getScore();
        scoreboard.getObjective("Score").getScore(teamColor).setScore(curr + 1);
        Bukkit.broadcast(Component.text("Nexus captured by " + teamColor));
    }
    public void NexusLost(String teamColor) {
        int curr = scoreboard.getObjective("Score").getScore(teamColor).getScore();
        scoreboard.getObjective("Score").getScore(teamColor).setScore(curr - 1);
        Bukkit.broadcast(Component.text(teamColor + "'s Nexus has been stolen!"));
    }

}
