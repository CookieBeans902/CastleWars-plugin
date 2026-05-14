package me.emumaps.managers;

import me.emumaps.utils.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;

public class KitManager {
    Map<String, Kit> kits;

    public KitManager() {
        kits = new java.util.HashMap<>();
    }

    public boolean kitExists(String kitName) {
        return kits.containsKey(kitName.toLowerCase());
    }

    public List<String> getKitNames() {
        return kits.values().stream().map(Kit::getDisplayName).toList();
    }

    public boolean loadKit(Player player, String kitName) {
        Kit kit = kits.get(kitName.toLowerCase());
        if (kit == null) {
            return false;
        }
        kit.applyKit(player.getInventory());
        return true;
    }

    public boolean loadEquipment(EntityEquipment inv, String kitName) {
        Kit kit = kits.get(kitName.toLowerCase());
        if (kit == null) {
            return false;
        }
        kit.applyEquipment(inv);
        return true;
    }

    public void addKit(String kitName, PlayerInventory inventory) {
        kits.put(kitName.toLowerCase(), new Kit(kitName,inventory));
    }
}
