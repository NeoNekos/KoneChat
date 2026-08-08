package cc.yamrc.konechat.hypertext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class ComponentPlanParser {
    private ComponentPlanParser() {
    }

    public static ComponentPlan parse(List<MarkupToken> tokens, HypertextConfig config) {
        if (tokens == null || config == null) throw new IllegalArgumentException("parser arguments must not be null");
        ComponentPlan.Builder out = new ComponentPlan.Builder();
        Deque<Scope> scopes = new ArrayDeque<>();
        StyleState style = StyleState.EMPTY;
        List<ComponentPlan.Gradient> gradients = new ArrayList<>();
        List<ComponentPlan.ColorLayer> colors = new ArrayList<>();
        long nextGradientId = 1L;

        for (MarkupToken token : tokens) {
            if (token instanceof MarkupToken.Text text) {
                out.literal(text.value(), text.matchable(), style, gradients, colors);
                continue;
            }
            if (token instanceof MarkupToken.Legacy legacy) {
                StyleOperation operation = StyleOperation.fromLegacy(legacy.code()).orElse(null);
                if (operation == null) {
                    out.literal(String.valueOf(config.legacyPrefix()) + legacy.code(), true, style, gradients, colors);
                } else if (operation.kind() == StyleOperation.Kind.RESET) {
                    scopes.clear();
                    style = StyleState.EMPTY;
                    gradients = new ArrayList<>();
                    colors = new ArrayList<>();
                } else {
                    style = style.apply(operation);
                    if (operation.kind() == StyleOperation.Kind.COLOR) {
                        colors = new ArrayList<>(colors);
                        colors.add(new ComponentPlan.FixedColor(operation.color()));
                    }
                }
                continue;
            }
            if (token instanceof MarkupToken.Open open) {
                String name = open.value();
                if (name.equals("reset")) {
                    scopes.clear();
                    style = StyleState.EMPTY;
                    gradients = new ArrayList<>();
                    colors = new ArrayList<>();
                    continue;
                }
                if (scopes.size() >= config.maxScopeDepth()) {
                    throw new HypertextException("hypertext scope depth exceeded");
                }
                if (name.startsWith("gradient:")) {
                    List<Integer> stops = parseStops(name, config.maxGradientStops());
                    scopes.push(new Scope("gradient", style, gradients, colors));
                    ComponentPlan.Gradient gradient = new ComponentPlan.Gradient(nextGradientId++, stops);
                    gradients = new ArrayList<>(gradients);
                    gradients.add(gradient);
                    colors = new ArrayList<>(colors);
                    colors.add(new ComponentPlan.GradientColor(gradient));
                    continue;
                }
                StyleOperation operation = StyleOperation.fromTag(name).orElse(null);
                if (operation == null) {
                    out.literal("<" + name + ">", true, style, gradients, colors);
                    continue;
                }
                scopes.push(new Scope(name, style, gradients, colors));
                style = style.apply(operation);
                if (operation.kind() == StyleOperation.Kind.COLOR) {
                    colors = new ArrayList<>(colors);
                    colors.add(new ComponentPlan.FixedColor(operation.color()));
                }
                continue;
            }
            MarkupToken.Close close = (MarkupToken.Close) token;
            if (!scopes.isEmpty() && scopes.peek().name().equals(close.value())) {
                Scope scope = scopes.pop();
                style = scope.style();
                gradients = new ArrayList<>(scope.gradients());
                colors = new ArrayList<>(scope.colors());
            } else {
                out.literal("</" + close.value() + ">", true, style, gradients, colors);
            }
        }
        return out.build();
    }

    private static List<Integer> parseStops(String value, int max) {
        String[] pieces = value.split(":", -1);
        if (pieces.length < 3 || pieces.length - 1 > max) {
            throw new HypertextException("gradient stop count exceeds configured limit");
        }
        List<Integer> stops = new ArrayList<>();
        for (int i = 1; i < pieces.length; i++) {
            if (!MarkupLexer.isHex(pieces[i])) throw new HypertextException("invalid gradient stop");
            stops.add(Integer.parseInt(pieces[i].substring(1), 16));
        }
        return stops;
    }

    private record Scope(String name, StyleState style, List<ComponentPlan.Gradient> gradients,
                         List<ComponentPlan.ColorLayer> colors) {
    }
}
