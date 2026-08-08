package cc.yamrc.konechat.channel;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public final class ChannelRuntimeState {
    private final Set<ResourceLocation> disabled = new HashSet<>();

    public boolean isDisabled(ResourceLocation id) { return disabled.contains(id); }
    public void disable(ResourceLocation id) { disabled.add(id); }
    public void enable(ResourceLocation id) { disabled.remove(id); }
    public void clear() { disabled.clear(); }
}
