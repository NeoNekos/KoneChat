package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.api.DirectConditionContext;
import cc.yamrc.konechat.api.DirectFormatContext;
import cc.yamrc.konechat.api.DirectMessageFormat;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public record DirectMessageDefinition(
        Function<DirectConditionContext, Boolean> condition,
        DirectMessageFormat format
) {
    public DirectMessageDefinition {
        if (condition == null || format == null) {
            throw new IllegalArgumentException("direct message definition is incomplete");
        }
    }

    public static DirectMessageDefinition defaults() {
        return new DirectMessageDefinition(context -> true, DirectMessageDefinition::defaultFormat);
    }

    private static Component defaultFormat(DirectFormatContext context) {
        return Component.literal("[")
                .append(context.sender.getDisplayName())
                .append(" -> ")
                .append(context.target.getDisplayName())
                .append("] ")
                .append(context.message);
    }
}
