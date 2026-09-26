package me.emumaps.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class NexusUtils {

    private NexusUtils() {}

    public static void spawnNexusItem(Location loc, int count) {
        ItemStack beaconItem = new ItemStack(Material.BEACON,count);
        Item itemEntity = loc.getWorld().dropItemNaturally(loc, beaconItem);
        itemEntity.setGlowing(true);
        itemEntity.setUnlimitedLifetime(true);
    }
}
