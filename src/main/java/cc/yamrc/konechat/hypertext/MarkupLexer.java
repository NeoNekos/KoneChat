package cc.yamrc.konechat.hypertext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MarkupLexer {
    private static final Set<String> SIMPLE_TAGS = Set.of(
            "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple",
            "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple",
            "yellow", "white", "bold", "italic", "underlined", "strikethrough", "obfuscated", "reset"
    );

    private MarkupLexer() {
    }

    public static List<MarkupToken> lex(String source, char legacyPrefix) {
        if (source == null || source.isEmpty()) return List.of();
        List<MarkupToken> result = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '\\' && index + 1 < source.length()) {
                flush(result, text, true);
                int end = escapedEnd(source, index + 1);
                appendText(result, source.substring(index + 1, end), false);
                index = end;
                continue;
            }
            if (current == '<') {
                int end = source.indexOf('>', index + 1);
                if (end >= 0) {
                    String body = source.substring(index + 1, end);
                    MarkupToken token = tag(body);
                    if (token != null) {
                        flush(result, text, true);
                        result.add(token);
                        index = end + 1;
                        continue;
                    }
                }
            }
            if (current == legacyPrefix && index + 1 < source.length()
                    && StyleOperation.fromLegacy(source.charAt(index + 1)).isPresent()) {
                flush(result, text, true);
                result.add(new MarkupToken.Legacy(source.charAt(index + 1)));
                index += 2;
                continue;
            }
            text.append(current);
            index++;
        }
        flush(result, text, true);
        return List.copyOf(result);
    }

    private static MarkupToken tag(String body) {
        if (body.isEmpty()) return null;
        boolean closing = body.charAt(0) == '/';
        String value = (closing ? body.substring(1) : body).toLowerCase(Locale.ROOT);
        if (value.isEmpty()) return null;
        if (closing) {
            return isClosable(value) ? new MarkupToken.Close(value) : null;
        }
        if (SIMPLE_TAGS.contains(value) || StyleOperation.fromTag(value).isPresent() || isGradient(value)) {
            return new MarkupToken.Open(value);
        }
        return null;
    }

    private static boolean isClosable(String value) {
        return (SIMPLE_TAGS.contains(value) && !value.equals("reset"))
                || StyleOperation.fromTag(value).isPresent() || value.equals("gradient");
    }

    private static boolean isGradient(String value) {
        if (!value.startsWith("gradient:")) return false;
        String[] values = value.split(":", -1);
        if (values.length < 3) return false;
        for (int i = 1; i < values.length; i++) {
            if (!isHex(values[i])) return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isHex(String value) {
        if (value.length() != 7 || value.charAt(0) != '#') return false;
        for (int i = 1; i < value.length(); i++) {
            if (Character.digit(value.charAt(i), 16) < 0) return false;
        }
        return true;
    }

    private static int escapedEnd(String source, int start) {
        char escaped = source.charAt(start);
        if (escaped == '<') {
            int end = source.indexOf('>', start + 1);
            return end < 0 ? start + 1 : end + 1;
        }
        if (escaped == '[') {
            int end = source.indexOf(']', start + 1);
            return end < 0 ? start + 1 : end + 1;
        }
        return start + 1;
    }

    private static void flush(List<MarkupToken> result, StringBuilder text, boolean matchable) {
        if (!text.isEmpty()) {
            appendText(result, text.toString(), matchable);
            text.setLength(0);
        }
    }

    private static void appendText(List<MarkupToken> result, String value, boolean matchable) {
        if (value.isEmpty()) return;
        if (!matchable && !result.isEmpty()
                && result.get(result.size() - 1) instanceof MarkupToken.Text previous
                && !previous.matchable()) {
            result.set(result.size() - 1, new MarkupToken.Text(previous.value() + value, false));
            return;
        }
        result.add(new MarkupToken.Text(value, matchable));
    }
}
