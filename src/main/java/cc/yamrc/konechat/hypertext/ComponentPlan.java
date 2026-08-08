package cc.yamrc.konechat.hypertext;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ComponentPlan {
    private final List<Node> nodes;

    ComponentPlan(List<? extends Node> nodes) {
        this.nodes = List.copyOf(nodes);
    }

    public List<Node> nodes() {
        return nodes;
    }

    @SuppressWarnings("unused")
    public String displayText() {
        StringBuilder text = new StringBuilder();
        for (Node node : nodes) text.append(node.displayText());
        return text.toString();
    }

    public interface Node {
        String displayText();
        StyleState style();
        List<Gradient> gradients();
        List<ColorLayer> colors();
    }

    public record LiteralNode(String text, boolean matchable, StyleState style,
                              List<Gradient> gradients, List<ColorLayer> colors) implements Node {
        public LiteralNode {
            if (text == null || text.isEmpty()) throw new IllegalArgumentException("literal text must not be empty");
            style = style == null ? StyleState.EMPTY : style;
            gradients = List.copyOf(gradients == null ? List.of() : gradients);
            colors = List.copyOf(colors == null ? List.of() : colors);
        }

        @Override public String displayText() { return text; }
    }

    public record PendingMatchNode(String source, String text, StyleState style,
                                   List<Gradient> gradients, List<ColorLayer> colors,
                                   ComponentRule rule, Match match, Object context) implements Node {
        public PendingMatchNode {
            if (source == null || text == null || text.isEmpty() || rule == null || match == null) {
                throw new IllegalArgumentException("pending match is incomplete");
            }
            style = style == null ? StyleState.EMPTY : style;
            gradients = List.copyOf(gradients == null ? List.of() : gradients);
            colors = List.copyOf(colors == null ? List.of() : colors);
        }

        @Override public String displayText() { return text; }
    }

    public record ResolvedComponentNode(Component component, StyleState style,
                                        List<Gradient> gradients, List<ColorLayer> colors) implements Node {
        public ResolvedComponentNode {
            if (component == null) throw new IllegalArgumentException("component must not be null");
            style = style == null ? StyleState.EMPTY : style;
            gradients = List.copyOf(gradients == null ? List.of() : gradients);
            colors = List.copyOf(colors == null ? List.of() : colors);
        }

        @Override public String displayText() { return component.getString(); }
    }

    public record Gradient(long id, List<Integer> stops) {
        public Gradient {
            stops = List.copyOf(stops);
            if (stops.size() < 2) throw new IllegalArgumentException("gradient needs two stops");
        }
    }

    public sealed interface ColorLayer permits FixedColor, GradientColor {
    }

    public record FixedColor(int rgb) implements ColorLayer {
    }

    public record GradientColor(Gradient gradient) implements ColorLayer {
    }

    static final class Builder {
        private final List<Node> nodes = new ArrayList<>();

        void literal(String text, boolean matchable, StyleState style,
                     List<Gradient> gradients, List<ColorLayer> colors) {
            if (text == null || text.isEmpty()) return;
            if (!nodes.isEmpty() && nodes.get(nodes.size() - 1) instanceof LiteralNode previous
                    && previous.matchable() == matchable && previous.style().equals(style)
                    && previous.gradients().equals(gradients) && previous.colors().equals(colors)) {
                nodes.set(nodes.size() - 1, new LiteralNode(previous.text() + text, matchable,
                        style, gradients, colors));
            } else {
                nodes.add(new LiteralNode(text, matchable, style, gradients, colors));
            }
        }

        void add(Node node) { nodes.add(node); }
        ComponentPlan build() { return new ComponentPlan(nodes); }
    }
}
