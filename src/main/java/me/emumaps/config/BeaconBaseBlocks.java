package me.emumaps.config;

import org.bukkit.Material;

import java.util.HashMap;

public class BeaconBaseBlocks {

    HashMap<String, Material> teamBlocks;

    public BeaconBaseBlocks() {
        teamBlocks = new HashMap<>();
        teamBlocks.put("red", Material.RED_WOOL);
        teamBlocks.put("blue", Material.BLUE_WOOL);
    }

    public Material getBeaconHolderBlock(String teamColor) {
        return teamBlocks.get(teamColor);
    }
}
