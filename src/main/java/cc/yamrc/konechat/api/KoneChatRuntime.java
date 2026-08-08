package cc.yamrc.konechat.api;

import cc.yamrc.konechat.registry.RegistryManager;

public final class KoneChatRuntime {
    private static final RegistryManager REGISTRY = new RegistryManager();

    private KoneChatRuntime() {
    }

    public static RegistryManager registry() { return REGISTRY; }
}
