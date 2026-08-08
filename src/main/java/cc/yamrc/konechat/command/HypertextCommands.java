package cc.yamrc.konechat.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class HypertextCommands {
    private HypertextCommands() {
    }

    static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("preview")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(context -> preview(context.getSource(),
                                StringArgumentType.getString(context, "text"))));
    }

    private static int preview(CommandSourceStack source, String text) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Component component = cc.yamrc.konechat.platform.chat.ChatPipeline
                .renderHypertext(player, "konechat:preview", text, true);
        player.sendSystemMessage(component);
        return 1;
    }
}
