package cc.yamrc.konechat.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class DirectFormatContext {
    public final ServerPlayer sender;
    public final ServerPlayer target;
    public final MinecraftServer server;
    public final FormatContext.Route route;
    public final long generation;
    public final String rawText;
    public final String filteredText;
    public final Component message;
    public final Component upstream;
    public final boolean preview;

    public DirectFormatContext(ServerPlayer sender, ServerPlayer target, MinecraftServer server,
                               FormatContext.Route route, long generation, String rawText,
                               String filteredText, Component message, Component upstream,
                               boolean preview) {
        if (sender == null || target == null || server == null || route == null || generation < 1
                || rawText == null || filteredText == null || message == null || upstream == null) {
            throw new IllegalArgumentException("direct format context is incomplete");
        }
        this.sender = sender;
        this.target = target;
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
    public ServerPlayer getTarget() { return target; }
    public MinecraftServer getServer() { return server; }
    public FormatContext.Route getRoute() { return route; }
    public long getGeneration() { return generation; }
    public String getRawText() { return rawText; }
    public String getFilteredText() { return filteredText; }
    public Component getMessage() { return message; }
    public Component getUpstream() { return upstream; }
    public boolean isPreview() { return preview; }
}
