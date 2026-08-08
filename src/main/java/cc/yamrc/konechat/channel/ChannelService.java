package cc.yamrc.konechat.channel;

import cc.yamrc.konechat.api.ChannelConditionContext;
import cc.yamrc.konechat.registry.ChannelDefinition;
import cc.yamrc.konechat.registry.GlobalSettings;
import cc.yamrc.konechat.registry.RegistryManager;
import cc.yamrc.konechat.registry.RuntimeSnapshot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ChannelService {
    private final RegistryManager registry;
    private final ChannelRuntimeState runtime = new ChannelRuntimeState();
    private final Map<UUID, Long> noChannelUntil = new HashMap<>();

    public ChannelService(RegistryManager registry) {
        this.registry = registry;
    }

    public ChannelRuntimeState runtime() { return runtime; }

    public Optional<ResourceLocation> current(ServerPlayer player) {
        return PlayerChannelData.get(player).map(ResourceLocation::tryParse);
    }

    public Optional<ResourceLocation> reevaluate(ServerPlayer player) {
        noChannelUntil.remove(player.getUUID());
        return registry.snapshot().flatMap(snapshot -> active(player, snapshot));
    }

    public Optional<ResourceLocation> active(ServerPlayer player, RuntimeSnapshot snapshot) {
        Optional<ResourceLocation> current = current(player);
        if (current.isPresent() && snapshot.channels().containsKey(current.get())
                && !runtime.isDisabled(current.get())) return current;
        if (current.isPresent()) PlayerChannelData.set(player, null);
        return autoJoin(player, snapshot);
    }

    public boolean join(ServerPlayer player, ResourceLocation id, boolean force) {
        RuntimeSnapshot snapshot = ready();
        ChannelDefinition target = snapshot.channels().get(id);
        if (target == null || runtime.isDisabled(id)) return false;
        Optional<ResourceLocation> current = current(player);
        if (!force && current.isPresent() && snapshot.channels().containsKey(current.get())) {
            ChannelDefinition old = snapshot.channels().get(current.get());
            if (!condition(old.canLeave(), player, old.id(), ChannelConditionContext.Reason.LEAVE)) return false;
        }
        if (!force && !condition(target.canJoin(), player, id, ChannelConditionContext.Reason.JOIN)) return false;
        PlayerChannelData.set(player, id.toString());
        noChannelUntil.remove(player.getUUID());
        return true;
    }

    public boolean leave(ServerPlayer player) {
        RuntimeSnapshot snapshot = ready();
        Optional<ResourceLocation> current = current(player);
        if (current.isEmpty()) return false;
        ChannelDefinition channel = snapshot.channels().get(current.get());
        if (channel != null && !condition(channel.canLeave(), player, channel.id(), ChannelConditionContext.Reason.LEAVE)) return false;
        PlayerChannelData.set(player, null);
        return true;
    }

    public boolean canSend(ServerPlayer player, ChannelDefinition channel) {
        return condition(channel.canSend(), player, channel.id(), ChannelConditionContext.Reason.SEND);
    }

    public List<ServerPlayer> members(ServerPlayer sender, ResourceLocation id) {
        return members(sender.server, id);
    }

    public List<ServerPlayer> members(MinecraftServer server, ResourceLocation id) {
        return server.getPlayerList().getPlayers().stream()
                .filter(player -> current(player).filter(id::equals).isPresent()).toList();
    }

    public RuntimeSnapshot ready() {
        return registry.snapshot().orElseThrow(() -> new IllegalStateException("KoneChat registry is not ready"));
    }

    private Optional<ResourceLocation> autoJoin(ServerPlayer player, RuntimeSnapshot snapshot) {
        long now = System.currentTimeMillis();
        if (noChannelUntil.getOrDefault(player.getUUID(), 0L) > now) return Optional.empty();
        List<ChannelDefinition> candidates = snapshot.channels().values().stream()
                .filter(channel -> channel.autoJoin() && !runtime.isDisabled(channel.id()))
                .sorted(Comparator.comparingInt(ChannelDefinition::weight).reversed()
                        .thenComparing(channel -> channel.id().toString())).toList();
        for (ChannelDefinition candidate : candidates) {
            if (condition(candidate.canJoin(), player, candidate.id(), ChannelConditionContext.Reason.AUTO_JOIN)) {
                PlayerChannelData.set(player, candidate.id().toString());
                return Optional.of(candidate.id());
            }
        }
        GlobalSettings settings = snapshot.settings();
        noChannelUntil.put(player.getUUID(), now + settings.autoJoinDebounceMillis());
        return Optional.empty();
    }

    private boolean condition(java.util.function.Function<ChannelConditionContext, Boolean> callback,
                               ServerPlayer player, ResourceLocation id, ChannelConditionContext.Reason reason) {
        Boolean result = callback.apply(new ChannelConditionContext(player, player.server, id.toString(), reason));
        if (result == null) throw new IllegalStateException("channel condition returned null");
        return result;
    }
}
