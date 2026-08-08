package cc.yamrc.konechat.hypertext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ComponentRuleStrategies {
    private ComponentRuleStrategies() {
    }

    public static MatcherProvider literal(String value) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("literal matcher must not be empty");
        return input -> {
            List<Match> matches = new ArrayList<>();
            int from = 0;
            while (from < input.source().length()) {
                int start = input.source().indexOf(value, from);
                if (start < 0) break;
                matches.add(new Match(start, start + value.length(), java.util.Map.of(), null));
                from = start + value.length();
            }
            return matches;
        };
    }

    public static MatcherProvider regex(String expression) {
        if (expression == null || expression.isEmpty()) throw new IllegalArgumentException("regex matcher must not be empty");
        Pattern pattern = Pattern.compile(expression);
        return input -> {
            List<Match> matches = new ArrayList<>();
            Matcher matcher = pattern.matcher(input.source());
            while (matcher.find()) {
                java.util.Map<String, String> groups = new java.util.LinkedHashMap<>();
                for (int i = 1; i <= matcher.groupCount(); i++) groups.put(String.valueOf(i), matcher.group(i));
                matches.add(new Match(matcher.start(), matcher.end(), groups, null));
            }
            return matches;
        };
    }
}
