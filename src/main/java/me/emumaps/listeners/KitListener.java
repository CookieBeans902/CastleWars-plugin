package me.emumaps.listeners;

import me.emumaps.castlewars.CastleWars;
import me.emumaps.managers.KitManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class KitListener implements Listener {

    KitManager kitManager;

    public KitListener(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onPlayerInteractNPC(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getType() != EntityType.MANNEQUIN) return;
        Mannequin npc = (Mannequin) event.getRightClicked();

        NamespacedKey kitKey = new NamespacedKey(CastleWars.getInstance(),"kit_name");
        if(!npc.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) return;
        String kitName = npc.getPersistentDataContainer().get(kitKey, PersistentDataType.STRING);
        kitManager.loadKit(event.getPlayer(),kitName);
    }

}


