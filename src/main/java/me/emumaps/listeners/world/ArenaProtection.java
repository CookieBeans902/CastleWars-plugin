package me.emumaps.listeners.world;

import me.emumaps.utils.Permissions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ArenaProtection implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    // Prevent players from breaking blocks aside from allowed ones.
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().hasPermission(Permissions.DEBUG_BYPASS)) return;
        Block block = event.getBlock();
        if(block.getBlockData().getMaterial() != Material.BEACON) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    // Prevent players from placing blocks aside from allowed ones.
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().hasPermission(Permissions.DEBUG_BYPASS)) return;

        Block block = event.getBlock();
        if(block.getBlockData().getMaterial() != Material.BEACON) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    // Prevent players from throwing items that are not allowed
    public void onItemThrow(EntityDropItemEvent event) {
        if(event.getEntity().hasPermission("castlewars.debug.bypass")) return;
        if(event.getItemDrop().getItemStack().getType() != Material.BEACON) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    // Prevent players from picking up items that are not allowed as an extra measure.
    public void onItemPickup(EntityPickupItemEvent event) {
        if(event.getEntity().hasPermission("castlewars.debug.bypass")) return;
        if(event.getItem().getItemStack().getType() != Material.BEACON) {
            event.setCancelled(true);
        }
    }
}
