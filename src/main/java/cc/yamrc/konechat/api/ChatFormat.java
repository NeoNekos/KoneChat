package cc.yamrc.konechat.api;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface ChatFormat {
    Component render(FormatContext context);
}
