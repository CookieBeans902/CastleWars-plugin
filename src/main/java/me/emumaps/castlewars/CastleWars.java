package me.emumaps.castlewars;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.emumaps.commands.CastleWarsCommand;
import me.emumaps.commands.KitSystem;
import me.emumaps.managers.GameManager;
import me.emumaps.managers.KitManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class CastleWars extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println("[CastleWars] The plugin has been loaded!!");
        getServer().getPluginManager().registerEvents(this,this);

        GameManager gameManager = new GameManager();
        KitManager kitManager = new KitManager();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            // register your commands here ...
            final Commands registrar = event.registrar();
            new CastleWarsCommand(gameManager, registrar);
            new KitSystem(kitManager, registrar);
        });
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        event.joinMessage(Component.text("Welcome " + playerName + "!!", NamedTextColor.BLUE));
    }
}
