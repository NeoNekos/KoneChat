package cc.yamrc.konechat.hypertext;

public record StyleState(
        Integer color,
        Boolean bold,
        Boolean italic,
        Boolean underlined,
        Boolean strikethrough,
        Boolean obfuscated
) {
    public static final StyleState EMPTY = new StyleState(null, null, null, null, null, null);

    public StyleState apply(StyleOperation operation) {
        return switch (operation.kind()) {
            case COLOR -> new StyleState(operation.color(), bold, italic, underlined, strikethrough, obfuscated);
            case BOLD -> new StyleState(color, true, italic, underlined, strikethrough, obfuscated);
            case ITALIC -> new StyleState(color, bold, true, underlined, strikethrough, obfuscated);
            case UNDERLINED -> new StyleState(color, bold, italic, true, strikethrough, obfuscated);
            case STRIKETHROUGH -> new StyleState(color, bold, italic, underlined, true, obfuscated);
            case OBFUSCATED -> new StyleState(color, bold, italic, underlined, strikethrough, true);
            case RESET -> EMPTY;
        };
    }
}
