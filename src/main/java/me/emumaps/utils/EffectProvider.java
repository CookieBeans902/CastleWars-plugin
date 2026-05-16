package me.emumaps.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectProvider {

    private EffectProvider() {}

    public static void updateGlowingEffect(Player player) {
        if(player.getInventory().contains(Material.BEACON)) {
            PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING,PotionEffect.INFINITE_DURATION,1,false,false,false);
            player.addPotionEffect(glowing);
        }
        else {
            player.removePotionEffect(PotionEffectType.GLOWING);
        }
    }
}
