package cc.yamrc.konechat.hypertext;

public record MatchInput(String source, Object context) {
    public MatchInput {
        if (source == null) throw new IllegalArgumentException("match source must not be null");
    }
}
