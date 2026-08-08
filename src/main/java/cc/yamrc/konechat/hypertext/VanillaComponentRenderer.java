package cc.yamrc.konechat.hypertext;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class VanillaComponentRenderer {
    private VanillaComponentRenderer() {
    }

    public static Component render(ComponentPlan plan, int maxDepth, int maxNodes) {
        MutableComponent root = Component.empty();
        for (ComponentPlan.Node node : plan.nodes()) root.append(renderNode(node));
        validate(root, 1, maxDepth, new Budget(maxNodes));
        return root;
    }

    private static Component renderNode(ComponentPlan.Node node) {
        if (node instanceof ComponentPlan.LiteralNode literal) {
            return Component.literal(literal.text()).setStyle(style(literal.style(), true));
        }
        if (node instanceof ComponentPlan.ResolvedComponentNode resolved) {
            MutableComponent copy = resolved.component().copy();
            copy.setStyle(copy.getStyle().applyTo(style(resolved.style(), true)));
            return copy;
        }
        throw new HypertextException("unresolved dynamic component");
    }

    @SuppressWarnings("SameParameterValue")
    private static Style style(StyleState state, boolean color) {
        Style style = Style.EMPTY;
        if (color && state.color() != null) style = style.withColor(state.color());
        if (state.bold() != null) style = style.withBold(state.bold());
        if (state.italic() != null) style = style.withItalic(state.italic());
        if (state.underlined() != null) style = style.withUnderlined(state.underlined());
        if (state.strikethrough() != null) style = style.withStrikethrough(state.strikethrough());
        if (state.obfuscated() != null) style = style.withObfuscated(state.obfuscated());
        return style;
    }

    private static void validate(Component component, int depth, int maxDepth, Budget budget) {
        if (depth > maxDepth) throw new HypertextException("component depth limit exceeded");
        budget.consume();
        for (Component sibling : component.getSiblings()) validate(sibling, depth + 1, maxDepth, budget);
    }

    private static final class Budget {
        private final int max;
        private int used;
        private Budget(int max) {
            if (max < 1) throw new IllegalArgumentException("node limit must be positive");
            this.max = max;
        }
        private void consume() {
            if (++used > max) throw new HypertextException("component node limit exceeded");
        }
    }
}
