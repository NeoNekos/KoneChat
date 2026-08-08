package cc.yamrc.konechat.hypertext;

import java.util.List;

@FunctionalInterface
public interface MatcherProvider {
    List<Match> findAll(MatchInput input);
}
