package cc.yamrc.konechat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import cc.yamrc.konechat.command.KoneChatCommands;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "konechat";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                KoneChatCommands.register(event.getDispatcher()));
    }
}
