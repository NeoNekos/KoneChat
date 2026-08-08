package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.api.ChannelConditionContext;
import cc.yamrc.konechat.registry.ChannelDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class ChannelBuilder {
    private final ResourceLocation id;
    private boolean autoJoin;
    private int weight;
    private ResourceLocation formatId;
    private RhinoCallback canJoin;
    private RhinoCallback canLeave;
    private RhinoCallback canSend;

    public ChannelBuilder(ResourceLocation id) {
        this.id = id;
        this.formatId = id;
    }

    public ChannelBuilder autoJoin(boolean value) { autoJoin = value; return this; }
    public ChannelBuilder weight(int value) { weight = value; return this; }
    public ChannelBuilder format(String value) { formatId = ResourceIds.parse(value, "format id"); return this; }
    public ChannelBuilder canJoin(Object value) { canJoin = RhinoCallback.capture(value, "canJoin"); return this; }
    public ChannelBuilder canLeave(Object value) { canLeave = RhinoCallback.capture(value, "canLeave"); return this; }
    public ChannelBuilder canSend(Object value) { canSend = RhinoCallback.capture(value, "canSend"); return this; }

    ChannelDefinition build() {
        return new ChannelDefinition(id, formatId, autoJoin, weight,
                condition(canJoin, ChannelConditionContext.Reason.JOIN),
                condition(canLeave, ChannelConditionContext.Reason.LEAVE),
                condition(canSend, ChannelConditionContext.Reason.SEND));
    }

    private Function<ChannelConditionContext, Boolean> condition(RhinoCallback callback,
                                                                  ChannelConditionContext.Reason reason) {
        if (callback == null) return context -> true;
        return context -> {
            Object value = callback.call(new ChannelConditionContext(context.sender,
                    context.server, id.toString(), reason));
            if (!(value instanceof Boolean result)) {
                throw new IllegalArgumentException("channel condition must return boolean");
            }
            return result;
        };
    }
}
