package me.emumaps.managers;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class KitManager {
    Map<String,byte[]> kits;
    public KitManager() {
        kits = new java.util.HashMap<>();
    }
    public ItemStack[] loadKit(String kitName) {
        byte[] inv = kits.get(kitName);
        ItemStack[] decodedInv = ItemStack.deserializeItemsFromBytes(inv);
        return decodedInv;
    }
    public void addKit(String kitName, ItemStack[] inventory) {
        byte[] encodedInv = ItemStack.serializeItemsAsBytes(inventory);
        kits.put(kitName,encodedInv);
    }
}
