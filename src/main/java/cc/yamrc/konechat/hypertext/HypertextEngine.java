package cc.yamrc.konechat.hypertext;

import net.minecraft.network.chat.Component;

import java.util.List;

public final class HypertextEngine {
    private HypertextEngine() {
    }

    public static Component render(String rawText, HypertextConfig config,
                                   List<ComponentRule> rules, Object context) {
        if (rawText == null || config == null || rules == null) {
            throw new IllegalArgumentException("hypertext arguments must not be null");
        }
        ComponentPlan parsed = ComponentPlanParser.parse(MarkupLexer.lex(rawText, config.legacyPrefix()), config);
        ComponentPlan matched = ComponentMatcherEngine.match(parsed, rules, context, config.maxProviderCandidates());
        ComponentPlan resolved = DynamicComponentResolver.resolve(matched, config.maxHandlerInvocations());
        ComponentPlan laidOut = GradientLayout.apply(resolved);
        return VanillaComponentRenderer.render(laidOut, config.maxRenderedDepth(), config.maxRenderedNodes());
    }
}
