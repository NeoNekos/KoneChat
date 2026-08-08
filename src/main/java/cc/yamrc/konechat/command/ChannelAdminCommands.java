package cc.yamrc.konechat.command;

import cc.yamrc.konechat.channel.PlayerChannelData;
import cc.yamrc.konechat.platform.ServerTranslations;
import cc.yamrc.konechat.platform.chat.ChatPipeline;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class ChannelAdminCommands {
    private ChannelAdminCommands() {
    }

    static void attach(LiteralArgumentBuilder<CommandSourceStack> channel) {
        channel.then(Commands.literal("members")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((context, builder) -> CommandSuggestions.channels(builder))
                        .executes(context -> members(context.getSource(),
                                ResourceLocationArgument.getId(context, "id")))));
        channel.then(Commands.literal("disable")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((context, builder) -> CommandSuggestions.channels(builder))
                        .executes(context -> disable(context.getSource(),
                                ResourceLocationArgument.getId(context, "id")))));
        channel.then(Commands.literal("enable")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((context, builder) -> CommandSuggestions.channels(builder))
                        .executes(context -> enable(context.getSource(),
                                ResourceLocationArgument.getId(context, "id")))));
        channel.then(Commands.literal("kick")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((context, builder) -> CommandSuggestions.channels(builder))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> kick(context.getSource(),
                                        ResourceLocationArgument.getId(context, "id"),
                                        EntityArgument.getPlayer(context, "player"))))));
    }

    private static int members(CommandSourceStack source, ResourceLocation id) {
        String names = ChatPipeline.channels().members(source.getServer(), id).stream()
                .map(member -> member.getGameProfile().getName()).sorted().reduce((a, b) -> a + ", " + b).orElse("-");
        source.sendSuccess(() -> ServerTranslations.message(source, "konechat.channel.list", names), true);

        return 1;
    }

    private static int disable(CommandSourceStack source, ResourceLocation id) {
        ChatPipeline.channels().runtime().disable(id);
        source.sendSuccess(() -> ServerTranslations.message(source, "konechat.channel.disabled", id.toString()), true);

        return 1;
    }

    private static int enable(CommandSourceStack source, ResourceLocation id) {
        ChatPipeline.channels().runtime().enable(id);
        source.sendSuccess(() -> ServerTranslations.message(source, "konechat.channel.enabled", id.toString()), true);

        return 1;
    }

    private static int kick(CommandSourceStack source, ResourceLocation id, ServerPlayer target) {
        if (ChatPipeline.channels().current(target).filter(id::equals).isEmpty()) return 0;

        PlayerChannelData.set(target, null);
        target.sendSystemMessage(ServerTranslations.message(target, "konechat.channel.kicked", id.toString()));
        source.sendSuccess(() -> ServerTranslations.message(source, "konechat.channel.kick_success",
                target.getDisplayName()), true);

        return 1;
    }
}
