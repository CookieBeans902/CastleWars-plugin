package me.emumaps.castlewars;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.emumaps.commands.CastleWarsCommand;
import me.emumaps.commands.KitCommands;
import me.emumaps.listeners.BlockBreakListener;
import me.emumaps.listeners.KitListener;
import me.emumaps.managers.GameManager;
import me.emumaps.managers.KitManager;
import me.emumaps.utils.Keys;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CastleWars extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Keys.init(this);
        GameManager gameManager = new GameManager();
        KitManager kitManager = new KitManager();
        getServer().getPluginManager().registerEvents(new KitListener(kitManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            // register your commands here ...
            final Commands registrar = event.registrar();
            new CastleWarsCommand(gameManager, registrar);
            new KitCommands(kitManager, registrar);
        });
    }


    public static CastleWars getInstance() {
        return JavaPlugin.getPlugin(CastleWars.class);
    }
}
