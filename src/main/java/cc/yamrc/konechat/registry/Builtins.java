package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.api.HypertextContext;
import cc.yamrc.konechat.hypertext.Match;
import cc.yamrc.konechat.hypertext.MatcherProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Builtins {
    public static final ResourceLocation ONLINE_PLAYER = ResourceLocation.fromNamespaceAndPath("konechat", "online_player");
    public static final ResourceLocation HELD_ITEM = ResourceLocation.fromNamespaceAndPath("konechat", "held_item");
    public static final ResourceLocation MENTION = ResourceLocation.fromNamespaceAndPath("konechat", "mention");

    private Builtins() {
    }

    public static void install(RegistryDraft draft) {
        draft.component(new ComponentRuleDraft(HELD_ITEM, draft.generation(), 0,
                ComponentRuleDraft.Strategy.LITERAL, "[i]", context -> true, context -> {
            HypertextContext hypertext = requireContext(context.context());
            ItemStack stack = hypertext.sender().getMainHandItem();

            return Component.empty()
                    .append(Component.literal("[").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY)))
                    .append(
                            stack.getHoverName().copy()
                                    .withStyle(stack.getRarity().getStyleModifier())
                                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                                            HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(stack))))
                    )
                    .append(Component.literal("]").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY)));
        }));


        draft.provider(ONLINE_PLAYER, onlinePlayerProvider());
        draft.component(new ComponentRuleDraft(MENTION, draft.generation(), 0,
                ComponentRuleDraft.Strategy.PROVIDER, ONLINE_PLAYER.toString(), context -> true, context -> {
            HypertextContext hypertext = requireContext(context.context());
            if (!(context.match().getData() instanceof ServerPlayer target)) return null;

            if (!hypertext.preview()) {
                target.displayClientMessage(Component.empty().append(hypertext.sender().getDisplayName()).append(" Mentioned you!"), true);
                target.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 0.75F, 1.2F);
            }

            return Component.literal(context.matchedText()).withStyle(style -> style
                    .withColor(ChatFormatting.DARK_GREEN)
                    .withBold(true)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.literal("Mentioned by ").append(hypertext.sender().getDisplayName()))));
        }));
    }

    private static MatcherProvider onlinePlayerProvider() {
        return input -> {
            HypertextContext hypertext = requireContext(input.context());
            String source = input.source();
            List<Match> matches = new ArrayList<>();

            int cursor = 0;
            while (cursor < source.length()) {
                int marker = source.indexOf('@', cursor);
                if (marker < 0) break;
                ServerPlayer selected = null;
                int selectedEnd = -1;
                for (ServerPlayer candidate : hypertext.server().getPlayerList().getPlayers()) {
                    String name = candidate.getGameProfile().getName();
                    int end = marker + 1 + name.length();
                    if (end <= source.length() && source.startsWith(name, marker + 1)
                            && (selected == null || name.length() > selected.getGameProfile().getName().length())) {
                        selected = candidate;
                        selectedEnd = end;
                    }
                }
                if (selected != null) {
                    matches.add(new Match(marker, selectedEnd,
                            Map.of("name", selected.getGameProfile().getName()), selected));
                    cursor = selectedEnd;
                } else {
                    cursor = marker + 1;
                }
            }
            return matches;
        };
    }

    private static HypertextContext requireContext(Object value) {
        if (value instanceof HypertextContext context) return context;
        throw new IllegalArgumentException("built-in component rule requires HypertextContext");
    }
}
