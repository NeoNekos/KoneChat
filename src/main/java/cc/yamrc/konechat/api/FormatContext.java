package cc.yamrc.konechat.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class FormatContext {
    public final ServerPlayer sender;
    public final MinecraftServer server;
    public final Route route;
    public final long generation;
    public final String rawText;
    public final String filteredText;
    public final Component message;
    public final Component upstream;
    public final boolean preview;

    public FormatContext(ServerPlayer sender, MinecraftServer server, Route route, long generation,
                         String rawText, String filteredText, Component message, Component upstream,
                         boolean preview) {
        if (sender == null || server == null || route == null || generation < 1 || rawText == null
                || filteredText == null || message == null || upstream == null) {
            throw new IllegalArgumentException("format context is incomplete");
        }
        this.sender = sender;
        this.server = server;
        this.route = route;
        this.generation = generation;
        this.rawText = rawText;
        this.filteredText = filteredText;
        this.message = message;
        this.upstream = upstream;
        this.preview = preview;
    }

    public ServerPlayer getSender() { return sender; }
    public MinecraftServer getServer() { return server; }
    public Route getRoute() { return route; }
    public long getGeneration() { return generation; }
    public String getRawText() { return rawText; }
    public String getFilteredText() { return filteredText; }
    public Component getMessage() { return message; }
    public Component getUpstream() { return upstream; }
    public boolean isPreview() { return preview; }

    public static final class Route {
        public final Kind kind;
        public final String id;
        public final String formatId;

        public Route(Kind kind, String id, String formatId) {
            if (kind == null || id == null || formatId == null) {
                throw new IllegalArgumentException("route is incomplete");
            }
            this.kind = kind;
            this.id = id;
            this.formatId = formatId;
        }

        public static Route channel(String id, String formatId) {
            return new Route(Kind.CHANNEL, id, formatId);
        }

        public static Route direct(String id, String formatId) {
            return new Route(Kind.DIRECT, id, formatId);
        }

        public static Route preview() {
            return new Route(Kind.PREVIEW, "konechat:preview", "konechat:preview");
        }

        public Kind getKind() { return kind; }
        public String getId() { return id; }
        public String getFormatId() { return formatId; }
    }

    public enum Kind { CHANNEL, DIRECT, PREVIEW }
}
