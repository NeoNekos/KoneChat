package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.platform.chat.ChatPipeline;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Small server-side operations surface intended for KubeJS scripts. */
@SuppressWarnings("unused")
public final class KoneChatBindings {
    public boolean join(Object player, String id) {
        return channels().join(player(player), ResourceIds.parse(id, "channel id"), false);
    }

    public boolean forceJoin(Object player, String id) {
        return channels().join(player(player), ResourceIds.parse(id, "channel id"), true);
    }

    public boolean leave(Object player) {
        return channels().leave(player(player));
    }

    public boolean reevaluate(Object player) {
        return channels().reevaluate(player(player)).isPresent();
    }

    public ResourceLocation activeChannel(Object player) {
        return channels().current(player(player)).orElse(null);
    }

    public boolean sendDirect(Object sender, Object target, String text) {
        return ChatPipeline.sendDirect(player(sender), player(target), text);
    }

    public Component render(Object sender, String text) {
        return ChatPipeline.renderHypertext(player(sender), "konechat:kubejs", text, false);
    }

    private static cc.yamrc.konechat.channel.ChannelService channels() {
        return ChatPipeline.channels();
    }

    private static ServerPlayer player(Object value) {
        Object unwrapped = Wrapper.unwrapped(value);
        if (!(unwrapped instanceof ServerPlayer serverPlayer)) {
            throw new IllegalArgumentException("KoneChat API requires a ServerPlayer");
        }
        return serverPlayer;
    }
}
