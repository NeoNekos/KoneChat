package cc.yamrc.konechat.platform.chat;

import cc.yamrc.konechat.api.FormatContext;
import cc.yamrc.konechat.api.HypertextContext;
import cc.yamrc.konechat.api.KoneChatRuntime;
import cc.yamrc.konechat.channel.ChannelService;
import cc.yamrc.konechat.registry.ChannelDefinition;
import cc.yamrc.konechat.registry.RuntimeSnapshot;
import cc.yamrc.konechat.registry.RuntimeState;
import cc.yamrc.konechat.hypertext.HypertextEngine;
import cc.yamrc.konechat.platform.ServerTranslations;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.Optional;

public final class ChatPipeline {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ChannelService CHANNELS = new ChannelService(KoneChatRuntime.registry());

    private ChatPipeline() {
    }

    public static ChannelService channels() { return CHANNELS; }

    public static boolean sendDirect(ServerPlayer sender, ServerPlayer target, String rawText) {
        try {
            Component message = renderHypertext(sender, "konechat:direct", rawText, false);
            Component formatted = Component.literal("[")
                    .append(sender.getDisplayName())
                    .append(" -> ")
                    .append(target.getDisplayName())
                    .append("] ")
                    .append(message);
            sender.sendSystemMessage(formatted);
            if (target != sender) target.sendSystemMessage(formatted);
            return true;
        } catch (Throwable throwable) {
            LOGGER.error("KoneChat direct message failed for {}", sender.getGameProfile().getName(), throwable);
            return false;
        }
    }

    public static Component renderHypertext(ServerPlayer sender, String routeId, String rawText, boolean preview) {
        RuntimeSnapshot snapshot = KoneChatRuntime.registry().snapshot()
                .orElseThrow(() -> new IllegalStateException("KoneChat registry is not ready"));
        if (rawText == null) throw new IllegalArgumentException("message text must not be null");
        HypertextContext hypertext = new HypertextContext(sender, sender.server, routeId,
                rawText, rawText, Component.literal(routeId), preview);
        return HypertextEngine.render(rawText, snapshot.settings().hypertext(),
                snapshot.componentRules(), hypertext);
    }

    public static ChatOutcome handle(ServerPlayer sender, PlayerChatMessage message) {
        RuntimeState state = KoneChatRuntime.registry().state();
        if (!(state instanceof RuntimeState.Ready ready)) {
            return ChatOutcome.failed(ServerTranslations.message(sender, "konechat.chat.reloading"));
        }
        RuntimeSnapshot snapshot = ready.snapshot();
        String raw = message.signedContent();
        if (message.isFullyFiltered()) {
            return ChatOutcome.rejected(ServerTranslations.message(sender, "konechat.chat.filtered"));
        }
        String filtered = message.filterMask().apply(raw);
        Optional<net.minecraft.resources.ResourceLocation> active = CHANNELS.active(sender, snapshot);
        if (active.isEmpty()) {
            return snapshot.settings().noChannelFallback()
                    ? ChatOutcome.rejected(ServerTranslations.message(sender, "konechat.chat.no_channel"))
                    : ChatOutcome.pass();
        }
        ChannelDefinition channel = snapshot.channels().get(active.get());
        try {
            if (!CHANNELS.canSend(sender, channel)) {
                return ChatOutcome.rejected(ServerTranslations.message(sender, "konechat.chat.cannot_send"));
            }
            HypertextContext hypertext = new HypertextContext(sender, sender.server, active.get().toString(), raw,
                    filtered, message.decoratedContent(), false);
            Component parsed = HypertextEngine.render(filtered, snapshot.settings().hypertext(), snapshot.componentRules(), hypertext);
            FormatContext formatContext = new FormatContext(sender, sender.server,
                    FormatContext.Route.channel(channel.id().toString(), channel.formatId().toString()),
                    snapshot.generation(), raw, filtered, parsed, message.decoratedContent(), false);
            Component formatted = snapshot.formats().get(channel.formatId()).render(formatContext);
            if (formatted == null) {
                return ChatOutcome.rejected(ServerTranslations.message(sender, "konechat.chat.cancelled"));
            }
            for (ServerPlayer recipient : CHANNELS.members(sender, channel.id())) recipient.sendSystemMessage(formatted);
            return ChatOutcome.delivered();
        } catch (Throwable throwable) {
            LOGGER.error("KoneChat chat pipeline failed for {}", sender.getGameProfile().getName(), throwable);
            return ChatOutcome.failed(ServerTranslations.message(sender, "konechat.chat.failed"));
        }
    }
}
