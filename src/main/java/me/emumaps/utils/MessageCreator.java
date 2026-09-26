package me.emumaps.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MessageCreator {

    private MessageCreator() {}

    public static Component playerDeathMessage(DamageSource damageSource, String deadPlayerName) {
        Entity srcEntity = damageSource.getCausingEntity();
        if(srcEntity != null) {
            if(srcEntity instanceof Player player) {
                return MiniMessage.miniMessage().deserialize(
                "<gray> " + deadPlayerName + "<dark_red> was killed by </dark_red" + player.getName() + "</gray>"
                );
            }
        }
        return MiniMessage.miniMessage().deserialize(
            "<gray> " + deadPlayerName + "<dark_red> died to unforeseen circumstances. </dark_red> </gray>"
        );
    }
}
