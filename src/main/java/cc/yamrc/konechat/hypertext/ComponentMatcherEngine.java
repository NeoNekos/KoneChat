package cc.yamrc.konechat.hypertext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ComponentMatcherEngine {
    private ComponentMatcherEngine() {
    }

    public static ComponentPlan match(ComponentPlan source, List<ComponentRule> rules,
                                      Object context, int maxCandidates) {
        if (source == null || rules == null) throw new IllegalArgumentException("matcher input must not be null");
        ComponentPlan.Builder output = new ComponentPlan.Builder();
        for (ComponentPlan.Node node : source.nodes()) {
            if (!(node instanceof ComponentPlan.LiteralNode literal) || !literal.matchable()) {
                output.add(node);
                continue;
            }
            matchLiteral(output, literal, rules, context, maxCandidates);
        }
        return output.build();
    }

    private static void matchLiteral(ComponentPlan.Builder output, ComponentPlan.LiteralNode literal,
                                     List<ComponentRule> rules, Object context, int maxCandidates) {
        String source = literal.text();
        List<ComponentRule.Candidate> candidates = new ArrayList<>();
        for (ComponentRule rule : rules) candidates.addAll(rule.find(source, context, maxCandidates));
        candidates.removeIf(candidate -> candidate.match().getStart() < 0 || candidate.match().getEnd() > source.length());
        candidates.sort(Comparator.comparingInt((ComponentRule.Candidate c) -> c.match().getStart())
                .thenComparing(Comparator.comparingInt((ComponentRule.Candidate c) -> c.rule().weight()).reversed())
                .thenComparing(Comparator.comparingInt((ComponentRule.Candidate c) -> c.match().getEnd() - c.match().getStart()).reversed())
                .thenComparing(c -> c.rule().id()));

        int cursor = 0;
        while (cursor < source.length()) {
            int earliest = -1;
            for (ComponentRule.Candidate candidate : candidates) {
                if (candidate.match().getStart() >= cursor) {
                    earliest = candidate.match().getStart();
                    break;
                }
            }
            if (earliest < 0) {
                output.literal(source.substring(cursor), true, literal.style(), literal.gradients(), literal.colors());
                break;
            }
            if (earliest > cursor) {
                output.literal(source.substring(cursor, earliest), true, literal.style(), literal.gradients(), literal.colors());
                cursor = earliest;
            }

            ComponentRule.Candidate selected = null;
            for (ComponentRule.Candidate candidate : candidates) {
                if (candidate.match().getStart() != earliest) continue;
                ComponentRule.MatchContext matchContext = new ComponentRule.MatchContext(
                        source, candidate.match().text(source), candidate.match().getStart(), candidate.match().getEnd(),
                        candidate.match(), context);
                if (candidate.rule().accepts(matchContext)) {
                    selected = candidate;
                    break;
                }
            }
            if (selected == null) {
                int length = Character.charCount(source.codePointAt(cursor));
                output.literal(source.substring(cursor, cursor + length), true,
                        literal.style(), literal.gradients(), literal.colors());
                cursor += length;
                continue;
            }
            Match match = selected.match();
            output.add(new ComponentPlan.PendingMatchNode(source, match.text(source), literal.style(),
                    literal.gradients(), literal.colors(), selected.rule(), match, context));
            cursor = match.getEnd();
        }
    }
}
