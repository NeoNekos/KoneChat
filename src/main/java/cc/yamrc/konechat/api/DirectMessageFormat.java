package cc.yamrc.konechat.api;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface DirectMessageFormat {
    Component render(DirectFormatContext context);
}
