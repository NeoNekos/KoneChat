package cc.yamrc.konechat.api.kubejs;

import net.minecraft.resources.ResourceLocation;

final class ResourceIds {
    private ResourceIds() {
    }

    static ResourceLocation parse(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null) throw new IllegalArgumentException(name + " must be a namespaced ID: " + value);
        return id;
    }
}
