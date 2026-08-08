package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.registry.RegistryDraft;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public final class RegistryEventJS extends EventJS {
    private final RegistryDraft draft;

    public RegistryEventJS(RegistryDraft draft) { this.draft = draft; }

    public void channel(String id, Object definition) {
        ResourceLocation resourceId = ResourceIds.parse(id, "channel id");
        ChannelBuilder builder = new ChannelBuilder(resourceId);
        RhinoCallback.capture(definition, "channel definition").call(builder);
        draft.channel(builder.build());
    }

    public void format(String id, Object definition) {
        ResourceLocation resourceId = ResourceIds.parse(id, "format id");
        draft.format(resourceId, new FormatBuilder(definition).build());
    }

    public void component(String id, Object definition) {
        ResourceLocation resourceId = ResourceIds.parse(id, "component id");
        ComponentRuleBuilder builder = new ComponentRuleBuilder(resourceId, draft.generation());
        RhinoCallback.capture(definition, "component definition").call(builder);
        draft.component(builder.build());
    }

    public void settings(Object definition) {
        SettingsBuilder builder = new SettingsBuilder(draft.settings());
        RhinoCallback.capture(definition, "settings definition").call(builder);
        draft.settings(builder.build());
    }

    public void removeChannel(String id) { draft.removeChannel(ResourceIds.parse(id, "channel id")); }
    public void removeFormat(String id) { draft.removeFormat(ResourceIds.parse(id, "format id")); }
    public void removeComponent(String id) { draft.removeComponent(ResourceIds.parse(id, "component id")); }
}
