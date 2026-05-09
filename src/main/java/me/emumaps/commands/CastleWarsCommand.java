package me.emumaps.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.emumaps.managers.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class CastleWarsCommand {
    GameManager gameManager;

    private int give(CommandContext<CommandSourceStack> ctx) {
        Bukkit.broadcast(Component.text("Important Announcement!", NamedTextColor.GOLD));
        Entity entity = ctx.getSource().getExecutor();
        if(entity == null) return 0;
        Material type = entity.getPickItemStack().getType();
        entity.sendMessage("You have picked up a " + type.name() + "item");
        return Command.SINGLE_SUCCESS;
    }

    public CastleWarsCommand(GameManager gameManager,Commands registrar) {
        this.gameManager = gameManager;

        LiteralArgumentBuilder<CommandSourceStack> root = createCommand();

        registrar.register(root.build(), "The main CastleWars command");

        registrar.register(Commands.literal("cw")
                .redirect(root.build()).build(),"Alias");
    }

    LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> castleWars;
        castleWars = Commands.literal("castlewars")
                .then(Commands.literal("say").executes(ctx -> {
                    int v = give(ctx);
                    return v;
                }))
                .then(Commands.literal("work")
                    .then(Commands.argument("rate", IntegerArgumentType.integer(1,10))
                        .executes(ctx -> {
                            int rate = ctx.getArgument("rate", Integer.class);
                            Entity entity = ctx.getSource().getExecutor();
                            if(entity == null) return 0;
                            entity.sendMessage("You have worked for " + rate + " hours");
                            return Command.SINGLE_SUCCESS;
                        })));

        return castleWars;
    }
}
