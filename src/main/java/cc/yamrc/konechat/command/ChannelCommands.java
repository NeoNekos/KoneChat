package cc.yamrc.konechat.command;

import cc.yamrc.konechat.platform.chat.ChatPipeline;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class ChannelCommands {
    private ChannelCommands() {
    }

    static LiteralArgumentBuilder<CommandSourceStack> create() {
        LiteralArgumentBuilder<CommandSourceStack> channel = Commands.literal("channel");
        channel.then(Commands.literal("join")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((context, builder) -> CommandSuggestions.channels(builder))
                        .executes(context -> join(context.getSource(),
                                ResourceLocationArgument.getId(context, "id"), false))
                        .then(Commands.argument("force", BoolArgumentType.bool())
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> join(context.getSource(),
                                        ResourceLocationArgument.getId(context, "id"),
                                        BoolArgumentType.getBool(context, "force"))))));
        channel.then(Commands.literal("leave")
                .executes(context -> leave(context.getSource())));
        ChannelAdminCommands.attach(channel);
        return channel;
    }

    private static int join(CommandSourceStack source, ResourceLocation id, boolean force) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean joined = ChatPipeline.channels().join(player, id, force);
        source.sendSuccess(() -> Component.literal(joined
                ? "Joined channel " + id + "."
                : "Could not join channel " + id + "."), true);
        return joined ? 1 : 0;
    }

    private static int leave(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean left = ChatPipeline.channels().leave(player);
        source.sendSuccess(() -> Component.literal(left
                ? "Left your current channel."
                : "Could not leave your current channel."), false);
        return left ? 1 : 0;
    }
}
