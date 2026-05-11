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

    static final String KIT_NAME = "Kit Name";

    public KitSystem(KitManager kitManager, Commands registrar) {
        this.kitManager = kitManager;
        LiteralArgumentBuilder<CommandSourceStack> kit;
        kit = createCommand();
        registrar.register(kit.build(), "Kit commands");
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> kit;
        kit = Commands.literal("cwkit");
        kit.then(Commands.literal("save")
                .requires(ctx -> ctx.getSender().hasPermission("castlewars.kit.save"))
                .then(Commands.argument(KIT_NAME, StringArgumentType.string())
                    .executes(ctx -> {
                            Entity entity = ctx.getSource().getExecutor();
                            String kitName = ctx.getArgument(KIT_NAME, String.class);
                            if (!(entity instanceof Player player)) {
                                return 0;
                            }
                            player.sendMessage("You have saved the kit " + kitName);
                            this.kitManager.addKit(kitName, player.getInventory().getContents());
                            return Command.SINGLE_SUCCESS;
                        }
                    )
                )
            )
            .then(Commands.literal("load")
                .then(Commands.argument(KIT_NAME, StringArgumentType.string())

                    .requires(ctx -> ctx.getSender().hasPermission("castlewars.kit.load"))
                    .suggests( (ctx,builder) -> {
                            String[] kitNames= kitManager.getKitNames();
                            String prefix = builder.getRemainingLowerCase();
                            for(String kitName:kitNames) {
                                if(!kitName.toLowerCase().startsWith(prefix)) continue;
                                builder.suggest(kitName);
                            }
                            return builder.buildFuture();
                        }
                    )
                    .executes(ctx -> {
                            Entity entity = ctx.getSource().getExecutor();
                            if (entity == null) return 0;
                            String kitName = ctx.getArgument(KIT_NAME, String.class);
                            if (entity instanceof Player player) {
                                ItemStack[] items = this.kitManager.loadKit(kitName);
                                if (items == null) {
                                    player.sendMessage("That kit does not exist");
                                    return 0;
                                }
                                player.sendMessage("You have loaded the kit " + kitName);
                                player.getInventory().setContents(items);
                                return Command.SINGLE_SUCCESS;
                            } else {
                                entity.sendMessage("You do not have permission to load kits");
                                return 0;
                            }
                        }
                    )
                )
            );
        return kit;
    }
}
