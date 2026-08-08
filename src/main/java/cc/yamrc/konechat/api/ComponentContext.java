package cc.yamrc.konechat.api;

import cc.yamrc.konechat.hypertext.Match;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public final class ComponentContext {
    public final ServerPlayer sender;
    public final MinecraftServer server;
    public final String routeId;
    public final String rawText;
    public final String filteredText;
    public final String source;
    public final String matchedText;
    public final int start;
    public final int end;
    public final Match match;
    public final Component upstream;
    public final boolean preview;

    public ComponentContext(ServerPlayer sender, MinecraftServer server, String routeId, String rawText,
                            String filteredText, String source, String matchedText, int start, int end,
                            Match match, Component upstream, boolean preview) {
        if (sender == null || server == null || rawText == null || filteredText == null
                || source == null || matchedText == null || match == null || upstream == null) {
            throw new IllegalArgumentException("component context is incomplete");
        }
        this.sender = sender;
        this.server = server;
        this.routeId = routeId;
        this.rawText = rawText;
        this.filteredText = filteredText;
        this.source = source;
        this.matchedText = matchedText;
        this.start = start;
        this.end = end;
        this.match = match;
        this.upstream = upstream;
        this.preview = preview;
    }

    public ServerPlayer getSender() { return sender; }
    public MinecraftServer getServer() { return server; }
    public String getRouteId() { return routeId; }
    public String getRawText() { return rawText; }
    public String getFilteredText() { return filteredText; }
    public String getSource() { return source; }
    public String getMatchedText() { return matchedText; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public Match getMatch() { return match; }
    public Component getUpstream() { return upstream; }
    public boolean isPreview() { return preview; }
}
