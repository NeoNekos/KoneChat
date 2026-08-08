package cc.yamrc.konechat.platform.mixin;

import cc.yamrc.konechat.api.kubejs.KoneChatPlugin;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScriptManager.class, remap = false)
public abstract class ScriptManagerMixin {
    @Shadow @Final public ScriptType scriptType;

    @Inject(method = "reload", at = @At("HEAD"), remap = false)
    private void konechat$beginReload(ResourceManager resources, CallbackInfo callbackInfo) {
        if (scriptType == ScriptType.SERVER) KoneChatPlugin.beforeServerScriptsReload();
    }

    @Inject(method = "reload", at = @At("RETURN"), remap = false)
    private void konechat$publishReload(ResourceManager resources, CallbackInfo callbackInfo) {
        if (scriptType == ScriptType.SERVER) KoneChatPlugin.afterServerScriptsReload();
    }
}
