package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.api.ComponentContext;
import cc.yamrc.konechat.api.HypertextContext;
import cc.yamrc.konechat.hypertext.ComponentRule;
import cc.yamrc.konechat.registry.ComponentRuleDraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class ComponentRuleBuilder {
    private final ResourceLocation id;
    private final long generation;
    private String value;
    private ComponentRuleDraft.Strategy strategy;
    private int weight;
    private RhinoCallback condition;
    private RhinoCallback handler;

    public ComponentRuleBuilder(ResourceLocation id, long generation) {
        this.id = id;
        this.generation = generation;
    }

    public ComponentRuleBuilder literal(String value) { select(ComponentRuleDraft.Strategy.LITERAL, value); return this; }
    public ComponentRuleBuilder regex(String value) { select(ComponentRuleDraft.Strategy.REGEX, value); return this; }
    public ComponentRuleBuilder matcher(String value) { select(ComponentRuleDraft.Strategy.PROVIDER, value); return this; }
    public ComponentRuleBuilder weight(int value) { weight = value; return this; }
    public ComponentRuleBuilder condition(Object value) { condition = RhinoCallback.capture(value, "component condition"); return this; }
    public ComponentRuleBuilder handler(Object value) { handler = RhinoCallback.capture(value, "component handler"); return this; }

    ComponentRuleDraft build() {
        if (strategy == null || handler == null) throw new IllegalArgumentException("component rule requires matcher and handler");
        Function<ComponentRule.MatchContext, Boolean> compiledCondition = context -> {
            if (condition == null) return true;
            Object value = condition.call(toScriptContext(context));
            if (!(value instanceof Boolean result)) throw new IllegalArgumentException("component condition must return boolean");
            return result;
        };
        Function<ComponentRule.MatchContext, Component> compiledHandler = context -> {
            Object value = handler.call(toScriptContext(context));
            if (value == null || value == dev.latvian.mods.rhino.Context.getUndefinedValue()) return null;
            if (!(value instanceof Component component)) throw new IllegalArgumentException("component handler must return Component or null");
            return component;
        };
        return new ComponentRuleDraft(id, generation, weight, strategy, value, compiledCondition, compiledHandler);
    }

    private Object toScriptContext(ComponentRule.MatchContext context) {
        if (!(context.context() instanceof HypertextContext hypertext)) {
            throw new IllegalArgumentException("component callback requires HypertextContext");
        }
        return new ComponentContext(hypertext.sender(), hypertext.server(), hypertext.routeId(), hypertext.rawText(),
                hypertext.filteredText(), context.source(), context.matchedText(), context.start(), context.end(),
                context.match(), hypertext.upstream(), hypertext.preview());
    }

    private void select(ComponentRuleDraft.Strategy next, String nextValue) {
        if (strategy != null) throw new IllegalArgumentException("component rule can select one matcher only");
        if (nextValue == null || nextValue.isEmpty()) throw new IllegalArgumentException("matcher value must not be empty");
        strategy = next;
        value = next == ComponentRuleDraft.Strategy.PROVIDER
                ? ResourceIds.parse(nextValue, "matcher provider id").toString() : nextValue;
    }
}
