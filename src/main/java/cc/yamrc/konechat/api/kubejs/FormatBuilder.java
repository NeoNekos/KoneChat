package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.api.ChatFormat;
import cc.yamrc.konechat.api.FormatContext;
import net.minecraft.network.chat.Component;

public final class FormatBuilder {
    private final RhinoCallback callback;

    public FormatBuilder(Object callback, long generation) {
        this.callback = RhinoCallback.capture(callback, "format callback");
    }

    ChatFormat build(long generation) {
        return context -> {
            Object value = callback.call(context);
            if (value == null || value == dev.latvian.mods.rhino.Context.getUndefinedValue()) return null;
            if (!(value instanceof Component component)) {
                throw new IllegalArgumentException("format callback must return Component or null");
            }
            return component;
        };
    }
}
