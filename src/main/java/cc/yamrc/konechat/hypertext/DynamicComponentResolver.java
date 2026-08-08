package cc.yamrc.konechat.hypertext;

import net.minecraft.network.chat.Component;

public final class DynamicComponentResolver {
    private DynamicComponentResolver() {
    }

    public static ComponentPlan resolve(ComponentPlan source, int maxInvocations) {
        if (maxInvocations < 1) throw new IllegalArgumentException("handler limit must be positive");
        ComponentPlan.Builder output = new ComponentPlan.Builder();
        int invocations = 0;
        for (ComponentPlan.Node node : source.nodes()) {
            if (!(node instanceof ComponentPlan.PendingMatchNode pending)) {
                output.add(node);
                continue;
            }
            if (++invocations > maxInvocations) throw new HypertextException("handler limit exceeded");
            ComponentRule.MatchContext context = new ComponentRule.MatchContext(
                    pending.source(), pending.text(), pending.match().getStart(), pending.match().getEnd(),
                    pending.match(), pending.context());
            Component replacement = pending.rule().handle(context);
            if (replacement == null) {
                output.literal(pending.text(), true, pending.style(), pending.gradients(), pending.colors());
            } else {
                output.add(new ComponentPlan.ResolvedComponentNode(replacement.copy(), pending.style(),
                        pending.gradients(), pending.colors()));
            }
        }
        return output.build();
    }
}
