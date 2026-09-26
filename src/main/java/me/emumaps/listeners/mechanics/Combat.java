package me.emumaps.listeners.mechanics;

import me.emumaps.utils.Keys;
import me.emumaps.utils.MessageCreator;
import me.emumaps.utils.NexusUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class Combat implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        int cnt = 0;
        for(ItemStack drop : drops) {
            if(drop.getType() == Material.BEACON) {
                if(Keys.checkBeaconKeyOnItem(drop)) {
                    cnt+=drop.getAmount();
                }
            }
        }
        drops.clear();
        Location loc = event.getPlayer().getLocation();
        Objects.requireNonNull(loc); // Unnecessary, but IntelliJ doesn't know that
        NexusUtils.spawnNexusItem(loc, cnt);
        event.deathMessage(MessageCreator.playerDeathMessage(event.getDamageSource(), event.getPlayer().getName()));
    }
}
