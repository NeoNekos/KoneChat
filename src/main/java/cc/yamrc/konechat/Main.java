package cc.yamrc.konechat;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import cc.yamrc.konechat.command.KoneChatCommands;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "konechat";

    public Main() {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                KoneChatCommands.register(event.getDispatcher()));
    }
}
