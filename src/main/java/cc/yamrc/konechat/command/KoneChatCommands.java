package cc.yamrc.konechat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public final class KoneChatCommands {
    private KoneChatCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("konechat");
        root.then(ChannelCommands.create());
        root.then(HypertextCommands.create());
        root.then(DirectMessageCommands.create());
        dispatcher.register(root);
        DirectMessageCommands.registerVanillaAliases(dispatcher);
    }
}
