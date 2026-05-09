package me.emumaps.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class KitManager {
    Map<String,Boolean> kitNames;
    public KitManager() {
        kitNames = new java.util.HashMap<>();
    }
    public void loadKit() {

    }
    public Boolean kitExists(String kitName) {
        if(kitNames.get(kitName)!= null) {
          return true;
        }
        return false;
    }
    public void addKit(String kitName, PlayerInventory inventory) {
        kitNames.put(kitName,true);
        ItemStack itemStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
        inventory.setChestplate(itemStack);
    }
}
