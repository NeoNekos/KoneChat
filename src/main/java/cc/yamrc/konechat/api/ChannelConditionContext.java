package cc.yamrc.konechat.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ChannelConditionContext {
    public final ServerPlayer sender;
    public final MinecraftServer server;
    public final String channelId;
    public final Reason reason;

    public ChannelConditionContext(ServerPlayer sender, MinecraftServer server, String channelId, Reason reason) {
        if (sender == null || server == null || channelId == null || reason == null) {
            throw new IllegalArgumentException("channel condition context is incomplete");
        }
        this.sender = sender;
        this.server = server;
        this.channelId = channelId;
        this.reason = reason;
    }

    public ServerPlayer getSender() { return sender; }
    public MinecraftServer getServer() { return server; }
    public String getChannelId() { return channelId; }
    public Reason getReason() { return reason; }

    public enum Reason { JOIN, LEAVE, SEND, AUTO_JOIN }
}
