package me.emumaps.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.math.FinePosition;
import me.emumaps.castlewars.CastleWars;
import me.emumaps.managers.KitManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class KitCommands {
    KitManager kitManager;

    static final String KIT_NAME = "Kit Name";

    // Regular Messages
    static final Component KIT_SAVED = Component.text("Kit saved successfully").color(TextColor.color(85, 85, 255));
    static final Component KIT_LOADED = Component.text("Kit loaded successfully").color(TextColor.color(85, 85, 255));

    // Error messages
    public static final Component KIT_ERROR = Component.text("[Error] Kit does not exist!").color(TextColor.color(179, 15, 15));
    public static final Component KIT_LOAD_ERROR = Component.text("[Error] Kit could not be loaded!").color(TextColor.color(179, 15, 15));
    public static final Component ONLY_PLAYER_COMMAND_ERROR = Component.text("[Error] Only Players can use this command!").color(TextColor.color(179, 15, 15));


    public KitCommands(KitManager kitManager, Commands registrar) {
        this.kitManager = kitManager;
        LiteralArgumentBuilder<CommandSourceStack> kit;
        kit = createCommand();
        registrar.register(kit.build(), "Kit commands");
    }

    // Methods

    private int npcSpawn(CommandContext<CommandSourceStack> ctx,
                         Component description) throws CommandSyntaxException {
        if (!kitManager.kitExists(ctx.getArgument(KIT_NAME, String.class))) {
            ctx.getSource().getExecutor().sendMessage(KIT_ERROR);
            return 0;
        }
        FinePosition pos = ctx.getArgument("Position", FinePositionResolver.class).resolve(ctx.getSource());
        Location loc = new Location(ctx.getSource().getExecutor().getWorld(), pos.x(), pos.y(), pos.z());
        Mannequin npc = ctx.getSource().getExecutor().getWorld().spawn(loc, Mannequin.class);
        npc.setInvulnerable(true);
        npc.customName(ctx.getArgument("Custom Name", Component.class));
        npc.setDescription(description);
        npc.setCustomNameVisible(true);
        PersistentDataContainer pdc = npc.getPersistentDataContainer();
        npc.setDescription(description);
        NamespacedKey kitKey = new NamespacedKey(CastleWars.getInstance(), "kit_name");
        pdc.set(kitKey, PersistentDataType.STRING, ctx.getArgument(KIT_NAME, String.class));
        boolean success = kitManager.loadEquipment(npc.getEquipment(), ctx.getArgument(KIT_NAME, String.class));
        if (!success) {
            ctx.getSource().getExecutor().sendMessage(KIT_LOAD_ERROR);
        }
        return Command.SINGLE_SUCCESS;
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
                            player.sendMessage(Component.text(kitName + " ").color(NamedTextColor.GREEN).append(KIT_SAVED));
                            this.kitManager.addKit(kitName, player.getInventory());
                            return Command.SINGLE_SUCCESS;
                        }
                    )
                )
            )
            .then(Commands.literal("load")
                .then(Commands.argument(KIT_NAME, StringArgumentType.string())
                    .requires(ctx -> ctx.getSender().hasPermission("castlewars.kit.load"))
                    .suggests((ctx, builder) -> {
                            List<String> kitNames = kitManager.getKitNames();
                            kitNames.stream()
                                .filter(kitName -> kitName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                .forEach(builder::suggest);
                            return builder.buildFuture();
                        }
                    )
                    .executes(ctx -> {
                            Entity entity = ctx.getSource().getExecutor();
                            if (entity == null) return 0;
                            String kitName = ctx.getArgument(KIT_NAME, String.class);
                            if (entity instanceof Player player) {
                                boolean success = kitManager.loadKit(player, kitName);
                                if (success) {
                                    player.sendMessage(Component.text(kitName + " ").color(NamedTextColor.GREEN).append(KIT_LOADED));
                                    return Command.SINGLE_SUCCESS;
                                }
                                player.sendMessage(KIT_LOAD_ERROR);
                            } else {
                                entity.sendMessage(ONLY_PLAYER_COMMAND_ERROR);
                            }
                            return 0;
                        }
                    )
                )
            )
            .then(Commands.literal("spawn")
                .then(Commands.argument("Position", ArgumentTypes.finePosition())
                    .then(Commands.argument("Rotation", ArgumentTypes.rotation())
                        .then(Commands.argument("Custom Name", ArgumentTypes.component())
                            .then(Commands.argument(KIT_NAME, StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                        List<String> kitNames = kitManager.getKitNames();
                                        kitNames.stream()
                                            .filter(kitName -> kitName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    }

                                )
                                .executes(ctx -> npcSpawn(ctx, null))
                                .then(Commands.argument("Description", ArgumentTypes.component())
                                    .executes(ctx -> npcSpawn(ctx,
                                            ctx.getArgument("Description", Component.class)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            );
        return kit;
    }
}
