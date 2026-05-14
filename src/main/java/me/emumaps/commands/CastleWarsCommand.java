package me.emumaps.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import me.emumaps.managers.GameManager;
import me.emumaps.utils.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class CastleWarsCommand {
    GameManager gameManager;

    private int give(CommandContext<CommandSourceStack> ctx) {
        Bukkit.broadcast(Component.text("Important Announcement!", NamedTextColor.GOLD));
        Entity entity = ctx.getSource().getExecutor();
        if (entity == null) return 0;
        Material type = entity.getPickItemStack().getType();
        entity.sendMessage("You have picked up a " + type.name() + "item");
        return Command.SINGLE_SUCCESS;
    }

    public CastleWarsCommand(GameManager gameManager, Commands registrar) {
        this.gameManager = gameManager;

        LiteralArgumentBuilder<CommandSourceStack> root = createCommand();

        registrar.register(root.build(), "The main CastleWars command");

        registrar.register(Commands.literal("cw")
            .redirect(root.build()).build(), "Alias");
    }

    LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> castleWars;
        castleWars = Commands.literal("castlewars")
            .then(Commands.literal("setblock")
                .then(Commands.argument("Position", ArgumentTypes.blockPosition())
                    .then(Commands.argument("Beacon Key", StringArgumentType.string())
                        .executes(ctx -> {
                                BlockPosition pos = ctx.getArgument("Position", BlockPositionResolver.class).resolve(ctx.getSource());
                                Location loc = new Location(ctx.getSource().getExecutor().getWorld(), pos.x(), pos.y(), pos.z());
                                loc.getBlock().setType(Material.BEACON);
                                BlockState state = loc.getBlock().getState();
                                if (state instanceof TileState tileable) {
                                    tileable.getPersistentDataContainer().set(Keys.BEACON_KEY, PersistentDataType.STRING, ctx.getArgument("Beacon Key", String.class));
                                    tileable.update();
                                    ctx.getSource().getExecutor().sendMessage("Beacon set!");
                                    return Command.SINGLE_SUCCESS;
                                }
                                return 0;
                            }
                        )
                    )
                )
            )
            .then(Commands.literal("set")
                .then(Commands.argument("Team Color Key", StringArgumentType.string())
                    .executes(ctx -> {
                            ctx.getSource().getExecutor().getPersistentDataContainer().set(Keys.TEAM_COLOR_KEY, PersistentDataType.STRING, ctx.getArgument("Team Color Key", String.class));
                            ctx.getSource().getExecutor().sendMessage("Team color set!");
                            return Command.SINGLE_SUCCESS;
                        }
                    )
                )
            )
            .then(Commands.literal("get")
                .executes(ctx -> {
                        Entity entity = ctx.getSource().getExecutor();
                        if (entity instanceof Player player) {
                            String res = player.getInventory().getItemInMainHand().getPersistentDataContainer().get(Keys.BEACON_KEY, PersistentDataType.STRING);
                            player.sendMessage(Component.text("Beacon Key: " + res));
                            return Command.SINGLE_SUCCESS;
                        }
                        return 0;
                    }
                )
            );

        return castleWars;
    }
}
