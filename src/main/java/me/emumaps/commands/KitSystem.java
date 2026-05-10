package me.emumaps.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.emumaps.managers.KitManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSystem {
    KitManager kitManager;
    public KitSystem(KitManager kitManager, Commands registrar) {
        this.kitManager = kitManager;
        LiteralArgumentBuilder<CommandSourceStack> kit;
        kit = createCommand();
        registrar.register(kit.build(), "Kit commands");
    }
    LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> kit;
        kit = Commands.literal("cwkit");
        kit.then(Commands.literal("save")
            .then(Commands.argument("Kit Name", StringArgumentType.string())
                            .executes(ctx -> {
                                    Entity entity = ctx.getSource().getExecutor();
                                    if (entity == null) return 0;
                                    String kitName = ctx.getArgument("Kit Name", String.class);
                                    if (!(entity instanceof Player player)) {
                                        return 0;
                                    }
                                    if (player.hasPermission("castlewars.kit.save")) {
                                        player.sendMessage("You have saved the kit " + kitName);
                                        this.kitManager.addKit(kitName,player.getInventory().getContents());
                                    }
                                    else {
                                        player.sendMessage("You do not have permission to save kits");
                                        return 0;
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }
                                )
            ))
            .then(Commands.literal("load")
                .then(Commands.argument("Kit Name", StringArgumentType.string())
                        .executes(ctx -> {
                            Entity entity = ctx.getSource().getExecutor();
                            if(entity == null) return 0;
                            String kitName = ctx.getArgument("Kit Name", String.class);
                            if(entity instanceof Player player) {
                                ItemStack[] items = this.kitManager.loadKit(kitName);
                                if(items == null) {
                                    player.sendMessage("That kit does not exist");
                                    return 0;
                                }
                                player.sendMessage("You have loaded the kit " + kitName);
                                player.getInventory().setContents(items);
                                return 0;
                            }
                            else {
                                return 0;
                            }
                        })
        ));
        return kit;
    }
}
