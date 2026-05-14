package me.emumaps.listeners;

import me.emumaps.utils.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class BlockBreakListener implements Listener {

    public BlockBreakListener() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(@NonNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        event.setDropItems(false);
        if (block.getBlockData().getMaterial() != Material.BEACON) {
            event.setCancelled(true);
            return;
        }
        if (!player.getPersistentDataContainer().has(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
            return;
        }
        TileState tileStateBlock = (TileState) block.getState();
        if (tileStateBlock.getPersistentDataContainer().has(Keys.BEACON_KEY, PersistentDataType.STRING)) {
            String beaconTeam = tileStateBlock.getPersistentDataContainer().get(Keys.BEACON_KEY, PersistentDataType.STRING);
            String playerTeam = player.getPersistentDataContainer().get(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING);
            if (Objects.equals(playerTeam, beaconTeam)) {
                player.sendMessage(Component.text()
                    .append(Component.text("[WARNING] ", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD))
                    .append(Component.text("You cannot mine your own block!").color(TextColor.color(255, 65, 78)))
                );
                event.setCancelled(true);
                return;
            }
        }
        ItemStack item = new ItemStack(Material.BEACON);
        ItemStack test = new ItemStack(Material.BEACON);
        player.getInventory().addItem(test);
        player.sendMessage(Component.text("Beacon not coming? WHAT???"));
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        String teamColor = player.getPersistentDataContainer().get(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING);
        pdc.set(Keys.BEACON_KEY, PersistentDataType.STRING, teamColor);
        item.setItemMeta(itemMeta);
    }
}
