package me.emumaps.managers;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class KitManager {
    Map<String, byte[]> kits;

    public KitManager() {
        kits = new java.util.HashMap<>();
    }

    public String[] getKitNames() {
        return kits.keySet().toArray(new String[0]);
    }

    public ItemStack[] loadKit(String kitName) {
        byte[] inv = kits.get(kitName);
        if (inv == null) return new ItemStack[0];
        return ItemStack.deserializeItemsFromBytes(inv);
    }

    public void addKit(String kitName, ItemStack[] inventory) {
        byte[] encodedInv = ItemStack.serializeItemsAsBytes(inventory);
        kits.put(kitName, encodedInv);
    }
}
