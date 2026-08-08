package cc.yamrc.konechat.api.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public final class KoneChatEvents {
    public static final EventGroup GROUP = EventGroup.of("KoneChatEvents");
    public static final EventHandler REGISTRY = GROUP.server("registry", () -> RegistryEventJS.class);

    private KoneChatEvents() {
    }

    public static void register() { GROUP.register(); }
}
