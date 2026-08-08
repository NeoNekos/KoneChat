package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.api.ChatFormat;
import net.minecraft.network.chat.Component;

public final class FormatBuilder {
    private final RhinoCallback callback;

    public FormatBuilder(Object callback) {
        this.callback = RhinoCallback.capture(callback, "format callback");
    }

    ChatFormat build() {
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
