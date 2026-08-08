package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.hypertext.HypertextConfig;

public record GlobalSettings(boolean noChannelFallback, long autoJoinDebounceMillis,
                             HypertextConfig hypertext) {
    public GlobalSettings {
        if (autoJoinDebounceMillis < 0 || hypertext == null) {
            throw new IllegalArgumentException("global settings are invalid");
        }
    }

    public static GlobalSettings defaults() {
        return new GlobalSettings(false, 1000L, HypertextConfig.defaults());
    }
}
