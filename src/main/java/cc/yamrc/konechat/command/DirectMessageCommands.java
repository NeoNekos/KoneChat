package cc.yamrc.konechat.command;

import cc.yamrc.konechat.platform.chat.ChatPipeline;
import cc.yamrc.konechat.platform.ServerTranslations;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

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

    private static int message(CommandSourceStack source, ServerPlayer target, String message) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayerOrException();
        if (!ChatPipeline.sendDirect(sender, target, message)) {
            source.sendFailure(ServerTranslations.message(sender, "konechat.chat.failed"));

            return 0;
        }

        return 1;
    }
}
