package cc.yamrc.konechat.registry;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class RegistryManager {
    private final AtomicReference<RuntimeState> state = new AtomicReference<>(RuntimeState.Reloading.INSTANCE);
    private long generation;

    public RuntimeState state() { return state.get(); }

    public Optional<RuntimeSnapshot> snapshot() {
        return state.get() instanceof RuntimeState.Ready ready
                ? Optional.of(ready.snapshot()) : Optional.empty();
    }

    public synchronized long beginReload() {
        state.set(RuntimeState.Reloading.INSTANCE);
        return ++generation;
    }

    public void publish(RuntimeSnapshot snapshot) {
        if (snapshot == null) throw new IllegalArgumentException("snapshot must not be null");
        state.set(new RuntimeState.Ready(snapshot));
    }

    public void broken(String diagnosticId) {
        state.set(new RuntimeState.Broken(diagnosticId));
    }
}
