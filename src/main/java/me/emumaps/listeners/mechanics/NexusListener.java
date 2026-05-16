package me.emumaps.listeners.mechanics;

import me.emumaps.castlewars.CastleWars;
import me.emumaps.config.BeaconBaseBlocks;
import me.emumaps.utils.EffectProvider;
import me.emumaps.utils.ItemCreator;
import me.emumaps.utils.Keys;
import me.emumaps.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class NexusListener implements Listener {
    BeaconBaseBlocks beaconBaseBlocks;

    public NexusListener(BeaconBaseBlocks beaconBaseBlocks) {
        this.beaconBaseBlocks = beaconBaseBlocks;
    }

    // Handles item pickup
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeaconPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.hasPermission(Permissions.DEBUG_BYPASS)) return;
        ItemStack item = event.getItem().getItemStack();
        String teamColor = player.getPersistentDataContainer().get(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING);
        ItemStack beaconItem = ItemCreator.createNexusBeacon(teamColor);

        item.setItemMeta(beaconItem.getItemMeta());

        Bukkit.getScheduler().runTask(CastleWars.getInstance(), () -> EffectProvider.updateGlowingEffect(player));
    }

    // Handles block breaking
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(@NonNull BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Debug Bypass
        if (player.hasPermission(Permissions.DEBUG_BYPASS)) return;

        Block block = event.getBlock();
        event.setDropItems(false);

        // Get the beacon team color and player team color
        if (!(block.getState() instanceof TileState tileStateBlock)) {
            event.setCancelled(true);
            return;
        }

        String beaconTeam = tileStateBlock.getPersistentDataContainer().get(Keys.BEACON_KEY, PersistentDataType.STRING);
        String playerTeam = player.getPersistentDataContainer().get(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING);
        if (playerTeam == null) {
            event.setCancelled(true);
            return;
        }

        // Mining their team beacon is not allowed
        if (Objects.equals(playerTeam, beaconTeam)) {
            player.sendMessage(Component.text()
                .append(Component.text("[WARNING] ", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD))
                .append(Component.text("You cannot mine your own block!").color(TextColor.color(255, 65, 78)))
            );
            event.setCancelled(true);
            return;
        }

        ItemStack item = ItemCreator.createNexusBeacon(playerTeam);
        Item itemEntity = player.getWorld().dropItemNaturally(block.getLocation(), item);
        itemEntity.setGlowing(true);

        Bukkit.getScheduler().runTask(CastleWars.getInstance(), () -> EffectProvider.updateGlowingEffect(player));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeaconPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Permissions.DEBUG_BYPASS)) return;
        Block belowBlock = event.getBlockPlaced().getRelative(BlockFace.DOWN);
        String teamColor = player.getPersistentDataContainer().get(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING);
        Objects.requireNonNull(teamColor);
        if (belowBlock.getType() != this.beaconBaseBlocks.getBeaconHolderBlock(teamColor.toLowerCase())) {
            event.setCancelled(true);
            player.sendMessage(Component.text()
                .append(Component.text("[WARNING] ", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD))
                .append(Component.text("You can only place the beacon in your castle.").color(TextColor.color(255, 65, 78)))
            );
            return;
        }
        Bukkit.getScheduler().runTask(CastleWars.getInstance(), () -> EffectProvider.updateGlowingEffect(player));
    }
}
