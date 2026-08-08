package cc.yamrc.konechat.hypertext;

public record HypertextConfig(
        char legacyPrefix,
        int maxScopeDepth,
        int maxGradientStops,
        int maxProviderCandidates,
        int maxHandlerInvocations,
        int maxRenderedDepth,
        int maxRenderedNodes
) {
    public HypertextConfig {
        if (maxScopeDepth < 1 || maxGradientStops < 2 || maxProviderCandidates < 1
                || maxHandlerInvocations < 1 || maxRenderedDepth < 1 || maxRenderedNodes < 1) {
            throw new IllegalArgumentException("hypertext limits must be positive");
        }
    }

    public static HypertextConfig defaults() {
        return new HypertextConfig('&', 32, 16, 256, 32, 32, 512);
    }
}
