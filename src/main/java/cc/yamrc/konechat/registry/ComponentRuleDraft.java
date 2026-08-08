package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.hypertext.ComponentRule;
import cc.yamrc.konechat.hypertext.ComponentRuleStrategies;
import cc.yamrc.konechat.hypertext.MatcherProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;

public record ComponentRuleDraft(
        ResourceLocation id,
        long generation,
        int weight,
        Strategy strategy,
        String value,
        Function<ComponentRule.MatchContext, Boolean> condition,
        Function<ComponentRule.MatchContext, net.minecraft.network.chat.Component> handler
) {
    public ComponentRuleDraft {
        if (id == null || generation < 1 || strategy == null || value == null || value.isEmpty()
                || condition == null || handler == null) {
            throw new IllegalArgumentException("component rule draft is incomplete");
        }
    }

    ComponentRule compile(Map<ResourceLocation, MatcherProvider> providers) {
        MatcherProvider provider = switch (strategy) {
            case LITERAL -> ComponentRuleStrategies.literal(value);
            case REGEX -> ComponentRuleStrategies.regex(value);
            case PROVIDER -> {
                ResourceLocation providerId = ResourceLocation.parse(value);
                MatcherProvider found = providers.get(providerId);
                if (found == null) throw new IllegalArgumentException("unknown matcher provider: " + value);
                yield found;
            }
        };
        return new ComponentRule(id.toString(), weight, provider, condition, handler);
    }

    public enum Strategy { LITERAL, REGEX, PROVIDER }
}
