package cc.yamrc.konechat.api.kubejs;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;

public final class RhinoCallback {
    private final Function function;
    private final Scriptable scope;
    private final Context context;

    private RhinoCallback(Function function, Scriptable scope, Context context) {
        this.function = function;
        this.scope = scope;
        this.context = context;
    }

    public static RhinoCallback capture(Object value, String name) {
        Object unwrapped = Wrapper.unwrapped(value);
        if (!(unwrapped instanceof Function function)) {
            throw new IllegalArgumentException(name + " must be a JavaScript function");
        }
        Scriptable scope = function.getParentScope();
        if (scope == null) throw new IllegalArgumentException(name + " has no Rhino parent scope");
        ScriptManager manager = ScriptType.SERVER.manager.get();
        Context context = manager.context;
        if (context == null) throw new IllegalStateException("KubeJS server Rhino context is unavailable");
        return new RhinoCallback(function, scope, context);
    }

    public Object call(Object... arguments) {
        Object[] wrappedArguments = arguments.clone();
        for (int i = 0; i < wrappedArguments.length; i++) {
            Object argument = wrappedArguments[i];
            if (!(argument instanceof String || argument instanceof Number || argument instanceof Boolean)) {
                wrappedArguments[i] = context.getWrapFactory().wrap(context, scope, argument, null);
            }
        }
        Object value = context.callSync(function, scope, scope, wrappedArguments);
        if (value == null || value == Context.getUndefinedValue()) return value;
        return Wrapper.unwrapped(value);
    }
}
