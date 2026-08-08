package cc.yamrc.konechat.hypertext;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class ComponentRule {
    private final String id;
    private final int weight;
    private final MatcherProvider provider;
    private final Function<MatchContext, Boolean> condition;
    private final Function<MatchContext, Component> handler;

    public ComponentRule(String id, int weight, MatcherProvider provider,
                         Function<MatchContext, Boolean> condition,
                         Function<MatchContext, Component> handler) {
        if (id == null || id.isBlank() || !id.contains(":")) throw new IllegalArgumentException("rule id must be namespaced");
        this.id = id;
        this.weight = weight;
        this.provider = Objects.requireNonNull(provider, "provider");
        this.condition = condition == null ? context -> true : condition;
        this.handler = Objects.requireNonNull(handler, "handler");
    }

    public String id() { return id; }
    public int weight() { return weight; }
    public List<Candidate> find(String source, Object context, int maxCandidates) {
        List<Match> matches = provider.findAll(new MatchInput(source, context));
        if (matches == null) throw new HypertextException("matcher provider returned null: " + id);
        if (matches.size() > maxCandidates) throw new HypertextException("matcher candidate limit exceeded: " + id);
        List<Candidate> candidates = new ArrayList<>();
        for (Match match : matches) {
            if (match.getStart() < 0 || match.getEnd() <= match.getStart() || match.getEnd() > source.length()) {
                throw new HypertextException("matcher returned invalid range: " + id);
            }
            candidates.add(new Candidate(this, match));
        }
        return candidates;
    }

    public boolean accepts(MatchContext context) {
        Boolean result = condition.apply(context);
        if (result == null) throw new HypertextException("condition returned null: " + id);
        return result;
    }

    public Component handle(MatchContext context) {
        return handler.apply(context);
    }

    public record Candidate(ComponentRule rule, Match match) {
    }

    public record MatchContext(String source, String matchedText, int start, int end,
                               Match match, Object context) {
    }
}
