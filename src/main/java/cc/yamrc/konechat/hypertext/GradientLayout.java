package cc.yamrc.konechat.hypertext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class GradientLayout {
    private static final Pattern GRAPHEME = Pattern.compile("\\X");

    private GradientLayout() {
    }

    public static ComponentPlan apply(ComponentPlan source) {
        Map<ComponentPlan.Gradient, Integer> totals = new HashMap<>();
        for (ComponentPlan.Node node : source.nodes()) {
            if (node instanceof ComponentPlan.LiteralNode literal && !literal.gradients().isEmpty()) {
                int count = graphemeCount(literal.text());
                for (ComponentPlan.Gradient gradient : literal.gradients()) {
                    totals.merge(gradient, count, Integer::sum);
                }
            }
        }
        Map<ComponentPlan.Gradient, Integer> cursors = new HashMap<>();
        ComponentPlan.Builder out = new ComponentPlan.Builder();
        for (ComponentPlan.Node node : source.nodes()) {
            if (!(node instanceof ComponentPlan.LiteralNode literal) || literal.gradients().isEmpty()) {
                out.add(node);
                continue;
            }
            List<String> clusters = new ArrayList<>();
            var matcher = GRAPHEME.matcher(literal.text());
            while (matcher.find()) clusters.add(matcher.group());
            for (String cluster : clusters) {
                int color = effectiveColor(literal, cursors, totals);
                StyleState style = color < 0 ? literal.style() : new StyleState(color, literal.style().bold(),
                        literal.style().italic(), literal.style().underlined(), literal.style().strikethrough(),
                        literal.style().obfuscated());
                out.literal(cluster, literal.matchable(), style, literal.gradients(), literal.colors());
                for (ComponentPlan.Gradient gradient : literal.gradients()) {
                    cursors.merge(gradient, 1, Integer::sum);
                }
            }
        }
        return out.build();
    }

    private static int effectiveColor(ComponentPlan.LiteralNode literal,
                                      Map<ComponentPlan.Gradient, Integer> cursors,
                                      Map<ComponentPlan.Gradient, Integer> totals) {
        ComponentPlan.ColorLayer last = null;
        for (ComponentPlan.ColorLayer layer : literal.colors()) last = layer;
        if (last instanceof ComponentPlan.FixedColor fixed) return fixed.rgb();
        if (last instanceof ComponentPlan.GradientColor gradient) {
            int index = cursors.getOrDefault(gradient.gradient(), 0);
            int length = totals.getOrDefault(gradient.gradient(), 1);
            return sample(gradient.gradient(), index, length);
        }
        return literal.style().color() == null ? -1 : literal.style().color();
    }

    private static int graphemeCount(String text) {
        int count = 0;
        var matcher = GRAPHEME.matcher(text);
        while (matcher.find()) count++;
        return count;
    }

    private static int sample(ComponentPlan.Gradient gradient, int index, int length) {
        if (length <= 1) return gradient.stops().get(0);
        double position = (double) index / (length - 1);
        double scaled = position * (gradient.stops().size() - 1);
        int segment = Math.min((int) Math.floor(scaled), gradient.stops().size() - 2);
        double fraction = scaled - segment;
        int left = gradient.stops().get(segment);
        int right = gradient.stops().get(segment + 1);
        int r = interpolate((left >> 16) & 0xFF, (right >> 16) & 0xFF, fraction);
        int g = interpolate((left >> 8) & 0xFF, (right >> 8) & 0xFF, fraction);
        int b = interpolate(left & 0xFF, right & 0xFF, fraction);
        return (r << 16) | (g << 8) | b;
    }

    private static int interpolate(int left, int right, double fraction) {
        return (int) Math.round(left + (right - left) * fraction);
    }
}
