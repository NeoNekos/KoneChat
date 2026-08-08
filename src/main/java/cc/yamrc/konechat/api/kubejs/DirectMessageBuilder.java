package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.api.DirectConditionContext;
import cc.yamrc.konechat.api.DirectMessageFormat;
import cc.yamrc.konechat.registry.DirectMessageDefinition;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class DirectMessageBuilder {
    private RhinoCallback condition;
    private RhinoCallback format;

    public DirectMessageBuilder condition(Object value) {
        condition = RhinoCallback.capture(value, "direct message condition");
        return this;
    }

    public DirectMessageBuilder format(Object value) {
        format = RhinoCallback.capture(value, "direct message format");
        return this;
    }

    DirectMessageDefinition build() {
        Function<DirectConditionContext, Boolean> compiledCondition = condition == null
                ? context -> true
                : context -> {
                    Object value = condition.call(context);
                    if (!(value instanceof Boolean result)) {
                        throw new IllegalArgumentException("direct message condition must return boolean");
                    }
                    return result;
                };
        DirectMessageFormat compiledFormat = format == null
                ? DirectMessageDefinition.defaults().format()
                : context -> {
                    Object value = format.call(context);
                    if (value == null || value == dev.latvian.mods.rhino.Context.getUndefinedValue()) {
                        return null;
                    }
                    if (!(value instanceof Component component)) {
                        throw new IllegalArgumentException("direct message format must return Component or null");
                    }
                    return component;
                };
        return new DirectMessageDefinition(compiledCondition, compiledFormat);
    }
}
