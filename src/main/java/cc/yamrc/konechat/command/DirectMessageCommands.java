package cc.yamrc.konechat.command;

import cc.yamrc.konechat.platform.chat.ChatPipeline;
import cc.yamrc.konechat.platform.ServerTranslations;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

final class DirectMessageCommands {
    private DirectMessageCommands() {
    }

    static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("msg")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> message(context.getSource(),
                                        EntityArgument.getPlayer(context, "player"),
                                        StringArgumentType.getString(context, "message")))));
    }

    static void registerVanillaAliases(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String alias : List.of("msg", "tell", "w")) {
            dispatcher.register(vanillaShape(alias));
        }
    }

    private static LiteralArgumentBuilder<CommandSourceStack> vanillaShape(String alias) {
        return Commands.literal(alias)
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes(context -> vanillaMessage(context.getSource(),
                                        EntityArgument.getPlayers(context, "targets"),
                                        MessageArgument.getMessage(context, "message")))));
    }

    private static int vanillaMessage(CommandSourceStack source, Collection<ServerPlayer> targets,
                                      Component message) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayerOrException();
        int delivered = 0;
        for (ServerPlayer target : targets) {
            if (ChatPipeline.sendDirect(sender, target, message.getString())) delivered++;
        }
        if (delivered == 0) {
            source.sendFailure(ServerTranslations.message(sender, "konechat.chat.failed"));
        }
        return delivered;
    }

    private static int message(CommandSourceStack source, ServerPlayer target, String message) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayerOrException();
        if (!ChatPipeline.sendDirect(sender, target, message)) {
            source.sendFailure(ServerTranslations.message(sender, "konechat.chat.failed"));

            return 0;
        }

        return 1;
    }
}
