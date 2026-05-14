package me.emumaps.utils;

import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kit {
    String displayName;
    ItemStack[] storageItems;
    ItemStack[] armorItems;
    ItemStack offHandItem;
    ItemStack mainHandItem;

    public Kit(String displayName, PlayerInventory inventory) {
        this.displayName = displayName;
        this.storageItems = inventory.getStorageContents();
        this.armorItems = inventory.getArmorContents();
        this.offHandItem = inventory.getItemInOffHand();
        this.mainHandItem = inventory.getItemInMainHand();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void applyKit(PlayerInventory inventory) {
        inventory.setStorageContents(storageItems);
        inventory.setArmorContents(armorItems);
        inventory.setItemInOffHand(offHandItem);
    }

    public void applyEquipment(EntityEquipment equipment) {
        equipment.setArmorContents(armorItems);
        equipment.setItemInMainHand(mainHandItem);
        equipment.setItemInOffHand(offHandItem);
    }

}


