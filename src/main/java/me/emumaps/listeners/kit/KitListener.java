package me.emumaps.listeners.kit;

import me.emumaps.managers.KitManager;
import me.emumaps.utils.Keys;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class KitListener implements Listener {

    KitManager kitManager;

    public KitListener(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onPlayerInteractNPC(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getType() != EntityType.MANNEQUIN) return;
        Mannequin npc = (Mannequin) event.getRightClicked();

        if(!npc.getPersistentDataContainer().has(Keys.KIT_KEY, PersistentDataType.STRING)) return;
        String kitName = npc.getPersistentDataContainer().get(Keys.KIT_KEY, PersistentDataType.STRING);
        Objects.requireNonNull(kitName);
        kitManager.loadKit(event.getPlayer(),kitName);
    }

}


