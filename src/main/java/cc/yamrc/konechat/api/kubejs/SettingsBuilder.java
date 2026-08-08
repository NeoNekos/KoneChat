package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.hypertext.HypertextConfig;
import cc.yamrc.konechat.registry.GlobalSettings;

@SuppressWarnings("unused")
public final class SettingsBuilder {
    private boolean noChannelFallback;
    private long autoJoinDebounce;
    private HypertextConfig hypertext;

    SettingsBuilder(GlobalSettings source) {
        noChannelFallback = source.noChannelFallback();
        autoJoinDebounce = source.autoJoinDebounceMillis();
        hypertext = source.hypertext();
    }

    /**
     * Controls the terminal behavior when no channel matches a player.
     * True rejects the message; false leaves it to vanilla chat.
     */
    public SettingsBuilder noChannelFallback(boolean value) {
        noChannelFallback = value;
        return this;
    }
    public SettingsBuilder autoJoinDebounce(long value) { autoJoinDebounce = value; return this; }
    public SettingsBuilder legacyPrefix(char value) { hypertext = new HypertextConfig(value, hypertext.maxScopeDepth(), hypertext.maxGradientStops(), hypertext.maxProviderCandidates(), hypertext.maxHandlerInvocations(), hypertext.maxRenderedDepth(), hypertext.maxRenderedNodes()); return this; }
    public SettingsBuilder maxScopeDepth(int value) { hypertext = new HypertextConfig(hypertext.legacyPrefix(), value, hypertext.maxGradientStops(), hypertext.maxProviderCandidates(), hypertext.maxHandlerInvocations(), hypertext.maxRenderedDepth(), hypertext.maxRenderedNodes()); return this; }
    public SettingsBuilder maxGradientStops(int value) { hypertext = new HypertextConfig(hypertext.legacyPrefix(), hypertext.maxScopeDepth(), value, hypertext.maxProviderCandidates(), hypertext.maxHandlerInvocations(), hypertext.maxRenderedDepth(), hypertext.maxRenderedNodes()); return this; }
    public SettingsBuilder maxProviderCandidates(int value) { hypertext = new HypertextConfig(hypertext.legacyPrefix(), hypertext.maxScopeDepth(), hypertext.maxGradientStops(), value, hypertext.maxHandlerInvocations(), hypertext.maxRenderedDepth(), hypertext.maxRenderedNodes()); return this; }
    public SettingsBuilder maxHandlerInvocations(int value) { hypertext = new HypertextConfig(hypertext.legacyPrefix(), hypertext.maxScopeDepth(), hypertext.maxGradientStops(), hypertext.maxProviderCandidates(), value, hypertext.maxRenderedDepth(), hypertext.maxRenderedNodes()); return this; }
    public SettingsBuilder maxRenderedDepth(int value) { hypertext = new HypertextConfig(hypertext.legacyPrefix(), hypertext.maxScopeDepth(), hypertext.maxGradientStops(), hypertext.maxProviderCandidates(), hypertext.maxHandlerInvocations(), value, hypertext.maxRenderedNodes()); return this; }
    public SettingsBuilder maxRenderedNodes(int value) { hypertext = new HypertextConfig(hypertext.legacyPrefix(), hypertext.maxScopeDepth(), hypertext.maxGradientStops(), hypertext.maxProviderCandidates(), hypertext.maxHandlerInvocations(), hypertext.maxRenderedDepth(), value); return this; }

    GlobalSettings build() { return new GlobalSettings(noChannelFallback, autoJoinDebounce, hypertext); }
}
