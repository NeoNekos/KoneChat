package cc.yamrc.konechat.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class DirectConditionContext {
    public final ServerPlayer sender;
    public final ServerPlayer target;
    public final MinecraftServer server;
    public final String rawText;

    public DirectConditionContext(ServerPlayer sender, ServerPlayer target,
                                  MinecraftServer server, String rawText) {
        if (sender == null || target == null || server == null || rawText == null) {
            throw new IllegalArgumentException("direct condition context is incomplete");
        }
        this.sender = sender;
        this.target = target;
        this.server = server;
        this.rawText = rawText;
    }

    public ServerPlayer getSender() { return sender; }
    public ServerPlayer getTarget() { return target; }
    public MinecraftServer getServer() { return server; }
    public String getRawText() { return rawText; }
}
