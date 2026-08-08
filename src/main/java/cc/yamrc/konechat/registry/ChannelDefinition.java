package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.api.ChannelConditionContext;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public record ChannelDefinition(
        ResourceLocation id,
        ResourceLocation formatId,
        boolean autoJoin,
        int weight,
        Function<ChannelConditionContext, Boolean> canJoin,
        Function<ChannelConditionContext, Boolean> canLeave,
        Function<ChannelConditionContext, Boolean> canSend
) {
    public ChannelDefinition {
        if (id == null || formatId == null || canJoin == null || canLeave == null || canSend == null) {
            throw new IllegalArgumentException("channel definition is incomplete");
        }
    }
}
