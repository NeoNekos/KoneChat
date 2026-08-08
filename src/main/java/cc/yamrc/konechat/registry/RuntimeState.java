package cc.yamrc.konechat.registry;

public sealed interface RuntimeState permits RuntimeState.Ready, RuntimeState.Reloading, RuntimeState.Broken {
    record Ready(RuntimeSnapshot snapshot) implements RuntimeState {
    }
    enum Reloading implements RuntimeState { INSTANCE }
    record Broken(String diagnosticId) implements RuntimeState {
        public Broken {
            if (diagnosticId == null || diagnosticId.isBlank()) {
                throw new IllegalArgumentException("diagnostic id must not be blank");
            }
        }
    }
}
