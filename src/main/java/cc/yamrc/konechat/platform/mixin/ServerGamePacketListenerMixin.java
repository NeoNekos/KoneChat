package cc.yamrc.konechat.platform.mixin;

import cc.yamrc.konechat.platform.chat.ChatOutcome;
import cc.yamrc.konechat.platform.chat.ChatPipeline;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    private void konechat$intercept(PlayerChatMessage message, CallbackInfo callbackInfo) {
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        ChatOutcome outcome = ChatPipeline.handle(listener.player, message);
        if (outcome.type() == ChatOutcome.Type.PASS_TO_VANILLA) return;
        if (outcome.feedback() != null) listener.player.sendSystemMessage(outcome.feedback());
        konechat$detectRateSpam();
        callbackInfo.cancel();
    }

    @Invoker("detectRateSpam")
    abstract void konechat$detectRateSpam();
}
