package cc.yamrc.konechat.registry;

import cc.yamrc.konechat.api.ChatFormat;
import cc.yamrc.konechat.hypertext.ComponentRule;
import cc.yamrc.konechat.hypertext.MatcherProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record RuntimeSnapshot(
        long generation,
        Map<ResourceLocation, ChannelDefinition> channels,
        Map<ResourceLocation, ChatFormat> formats,
        List<ComponentRule> componentRules,
        Map<ResourceLocation, MatcherProvider> matcherProviders,
        GlobalSettings settings,
        DirectMessageDefinition directMessage
) {
    public RuntimeSnapshot {
        if (generation < 1 || settings == null || directMessage == null) {
            throw new IllegalArgumentException("runtime snapshot is invalid");
        }
        channels = Map.copyOf(channels);
        formats = Map.copyOf(formats);
        componentRules = List.copyOf(componentRules);
        matcherProviders = Map.copyOf(matcherProviders);
    }
}
