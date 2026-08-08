package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.api.ChatFormat;
import cc.yamrc.konechat.hypertext.ComponentRule;
import cc.yamrc.konechat.hypertext.MatcherProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RegistryDraft {
    private final long generation;
    private final Map<ResourceLocation, ChannelDefinition> channels = new LinkedHashMap<>();
    private final Map<ResourceLocation, ChatFormat> formats = new LinkedHashMap<>();
    private final Map<ResourceLocation, ComponentRuleDraft> componentRules = new LinkedHashMap<>();
    private final Map<ResourceLocation, MatcherProvider> matcherProviders = new LinkedHashMap<>();
    private GlobalSettings settings = GlobalSettings.defaults();
    private DirectMessageDefinition directMessage = DirectMessageDefinition.defaults();
    private boolean directMessageRegistered;

    public RegistryDraft(long generation) {
        if (generation < 1) throw new IllegalArgumentException("registry generation must be positive");
        this.generation = generation;
    }

    public long generation() { return generation; }
    public GlobalSettings settings() { return settings; }
    public void settings(GlobalSettings value) { settings = value; }
    public void channel(ChannelDefinition value) { register(channels, value.id(), value); }
    public void format(ResourceLocation id, ChatFormat value) { register(formats, id, value); }
    public void component(ComponentRuleDraft value) { register(componentRules, value.id(), value); }
    public void provider(ResourceLocation id, MatcherProvider value) { register(matcherProviders, id, value); }
    public void directMessage(DirectMessageDefinition value) {
        if (directMessageRegistered) {
            throw new IllegalArgumentException("direct message definition is already registered");
        }
        if (value == null) throw new IllegalArgumentException("direct message definition is incomplete");
        directMessage = value;
        directMessageRegistered = true;
    }
    public void removeChannel(ResourceLocation id) { remove(channels, id); }
    public void removeFormat(ResourceLocation id) { remove(formats, id); }
    public void removeComponent(ResourceLocation id) { remove(componentRules, id); }

    public RuntimeSnapshot compile() {
        for (ChannelDefinition channel : channels.values()) {
            if (!formats.containsKey(channel.formatId())) {
                throw new IllegalArgumentException("channel references unknown format: " + channel.formatId());
            }
        }
        List<ComponentRule> compiled = new ArrayList<>();
        for (ComponentRuleDraft rule : componentRules.values()) compiled.add(rule.compile(matcherProviders));
        compiled.sort(Comparator.comparingInt(ComponentRule::weight).reversed().thenComparing(ComponentRule::id));
        return new RuntimeSnapshot(generation, channels, formats, compiled, matcherProviders, settings,
                directMessage);
    }

    private static <T> void register(Map<ResourceLocation, T> map, ResourceLocation id, T value) {
        if (id == null || value == null) throw new IllegalArgumentException("registry entry is incomplete");
        if (map.putIfAbsent(id, value) != null) throw new IllegalArgumentException("duplicate registry id: " + id);
    }

    private static <T> void remove(Map<ResourceLocation, T> map, ResourceLocation id) {
        if (map.remove(id) == null) throw new IllegalArgumentException("registry id is not registered: " + id);
    }
}
