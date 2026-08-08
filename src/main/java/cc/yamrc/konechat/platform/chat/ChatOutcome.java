package cc.yamrc.konechat.platform.chat;

import net.minecraft.network.chat.Component;

public record ChatOutcome(Type type, Component feedback) {
    public enum Type { PASS_TO_VANILLA, DELIVERED, REJECTED, FAILED }

    public static ChatOutcome pass() { return new ChatOutcome(Type.PASS_TO_VANILLA, null); }
    public static ChatOutcome delivered() { return new ChatOutcome(Type.DELIVERED, null); }
    public static ChatOutcome rejected(Component feedback) { return new ChatOutcome(Type.REJECTED, feedback); }
    public static ChatOutcome failed(Component feedback) { return new ChatOutcome(Type.FAILED, feedback); }
}
