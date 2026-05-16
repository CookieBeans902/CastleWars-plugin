package me.emumaps.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemCreator {
    private ItemCreator() {
        /* This utility class should not be instantiated */
    }


    public static ItemStack createNexusBeacon(String beaconKey) {
        ItemStack beacon = new ItemStack(Material.BEACON);
        BlockStateMeta beaconMeta = (BlockStateMeta)beacon.getItemMeta();
        TileState blockState = (TileState)beaconMeta.getBlockState();
        blockState.getPersistentDataContainer().set(Keys.BEACON_KEY, PersistentDataType.STRING, beaconKey);
        beaconMeta.setBlockState(blockState);
        beaconMeta.customName(Component.text()
            .append(Component.text("Nexus ").color(TextColor.color(255, 127, 54))).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
            .append(Component.text("Beacon").color(TextColor.color(255, 127, 54))).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
            .build()
        );
        beacon.setItemMeta(beaconMeta);
        return beacon;
    }
}
