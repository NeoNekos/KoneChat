package cc.yamrc.konechat.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record HypertextContext(
        ServerPlayer sender,
        MinecraftServer server,
        String routeId,
        String rawText,
        String filteredText,
        Component upstream,
        boolean preview
) {
    public HypertextContext {
        if (sender == null || server == null || rawText == null || filteredText == null || upstream == null) {
            throw new IllegalArgumentException("hypertext context is incomplete");
        }
    }

    public ServerPlayer getSender() { return sender; }
    public MinecraftServer getServer() { return server; }
    public String getRouteId() { return routeId; }
    public String getRawText() { return rawText; }
    public String getFilteredText() { return filteredText; }
    public Component getUpstream() { return upstream; }
    public boolean isPreview() { return preview; }
}
