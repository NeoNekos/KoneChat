package cc.yamrc.konechat.api.kubejs;

import cc.yamrc.konechat.api.KoneChatRuntime;
import cc.yamrc.konechat.registry.Builtins;
import cc.yamrc.konechat.registry.RegistryDraft;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public final class KoneChatPlugin extends KubeJSPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("KoneChat KubeJS");
    private static long loadingGeneration;

    @Override
    public void registerEvents() {
        KoneChatEvents.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        if (event.getType().isServer()) event.add("KoneChat", new KoneChatBindings());
    }

    public static synchronized void beforeServerScriptsReload() {
        loadingGeneration = KoneChatRuntime.registry().beginReload();
    }

    public static synchronized void afterServerScriptsReload() {
        long generation = loadingGeneration;
        loadingGeneration = 0;
        if (generation < 1) generation = KoneChatRuntime.registry().beginReload();
        RegistryDraft draft = new RegistryDraft(generation);
        try {
            Builtins.install(draft);
            AtomicReference<Throwable> callbackFailure = new AtomicReference<>();
            KoneChatEvents.REGISTRY.post(ScriptType.SERVER, new RegistryEventJS(draft),
                    (event, container, throwable) -> {
                        callbackFailure.compareAndSet(null, throwable);
                        return throwable;
                    });
            if (callbackFailure.get() != null) {
                throw new IllegalStateException("KoneChat registry callback failed", callbackFailure.get());
            }
            var snapshot = draft.compile();
            KoneChatRuntime.registry().publish(snapshot);
            LOGGER.info("Published KoneChat generation {} with {} channels, {} formats and {} components",
                    snapshot.generation(), snapshot.channels().size(), snapshot.formats().size(),
                    snapshot.componentRules().size());
        } catch (Throwable throwable) {
            KoneChatRuntime.registry().broken("konechat.registry.reload_failed");
            LOGGER.error("KoneChat registry reload failed", throwable);
        }
    }
}
