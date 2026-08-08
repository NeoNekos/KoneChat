package cc.yamrc.konechat.hypertext;

import java.util.Locale;
import java.util.Optional;

public record StyleOperation(Kind kind, Integer color, Boolean value) {
    public enum Kind { COLOR, BOLD, ITALIC, UNDERLINED, STRIKETHROUGH, OBFUSCATED, RESET }

    public static Optional<StyleOperation> fromTag(String tag) {
        return switch (tag) {
            case "black" -> Optional.of(color(0x000000));
            case "dark_blue" -> Optional.of(color(0x0000AA));
            case "dark_green" -> Optional.of(color(0x00AA00));
            case "dark_aqua" -> Optional.of(color(0x00AAAA));
            case "dark_red" -> Optional.of(color(0xAA0000));
            case "dark_purple" -> Optional.of(color(0xAA00AA));
            case "gold" -> Optional.of(color(0xFFAA00));
            case "gray" -> Optional.of(color(0xAAAAAA));
            case "dark_gray" -> Optional.of(color(0x555555));
            case "blue" -> Optional.of(color(0x5555FF));
            case "green" -> Optional.of(color(0x55FF55));
            case "aqua" -> Optional.of(color(0x55FFFF));
            case "red" -> Optional.of(color(0xFF5555));
            case "light_purple" -> Optional.of(color(0xFF55FF));
            case "yellow" -> Optional.of(color(0xFFFF55));
            case "white" -> Optional.of(color(0xFFFFFF));
            case "bold" -> Optional.of(new StyleOperation(Kind.BOLD, null, true));
            case "italic" -> Optional.of(new StyleOperation(Kind.ITALIC, null, true));
            case "underlined" -> Optional.of(new StyleOperation(Kind.UNDERLINED, null, true));
            case "strikethrough" -> Optional.of(new StyleOperation(Kind.STRIKETHROUGH, null, true));
            case "obfuscated" -> Optional.of(new StyleOperation(Kind.OBFUSCATED, null, true));
            case "reset" -> Optional.of(new StyleOperation(Kind.RESET, null, null));
            default -> {
                if (isHex(tag)) {
                    yield Optional.of(color(Integer.parseInt(tag.substring(1), 16)));
                }
                yield Optional.empty();
            }
        };
    }

    public static Optional<StyleOperation> fromLegacy(char code) {
        char normalized = Character.toLowerCase(code);
        if (normalized >= '0' && normalized <= '9') {
            int[] colors = {0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000,
                    0xAA00AA, 0xFFAA00, 0xAAAAAA, 0x555555, 0x5555FF};
            return Optional.of(color(colors[normalized - '0']));
        }
        return switch (normalized) {
            case 'a' -> Optional.of(color(0x55FF55));
            case 'b' -> Optional.of(color(0x55FFFF));
            case 'c' -> Optional.of(color(0xFF5555));
            case 'd' -> Optional.of(color(0xFF55FF));
            case 'e' -> Optional.of(color(0xFFFF55));
            case 'f' -> Optional.of(color(0xFFFFFF));
            case 'k' -> Optional.of(new StyleOperation(Kind.OBFUSCATED, null, true));
            case 'l' -> Optional.of(new StyleOperation(Kind.BOLD, null, true));
            case 'm' -> Optional.of(new StyleOperation(Kind.STRIKETHROUGH, null, true));
            case 'n' -> Optional.of(new StyleOperation(Kind.UNDERLINED, null, true));
            case 'o' -> Optional.of(new StyleOperation(Kind.ITALIC, null, true));
            case 'r' -> Optional.of(new StyleOperation(Kind.RESET, null, null));
            default -> Optional.empty();
        };
    }

    private static StyleOperation color(int rgb) {
        return new StyleOperation(Kind.COLOR, rgb & 0xFFFFFF, true);
    }

    private static boolean isHex(String value) {
        if (value.length() != 7 || value.charAt(0) != '#') return false;
        for (int i = 1; i < value.length(); i++) {
            if (Character.digit(value.charAt(i), 16) < 0) return false;
        }
        return true;
    }
}
