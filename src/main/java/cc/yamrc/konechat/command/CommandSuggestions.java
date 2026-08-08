package cc.yamrc.konechat.command;

import cc.yamrc.konechat.api.KoneChatRuntime;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;

final class CommandSuggestions {
    private CommandSuggestions() {
    }

    static CompletableFuture<Suggestions> channels(SuggestionsBuilder builder) {
        KoneChatRuntime.registry().snapshot().ifPresent(snapshot ->
                SharedSuggestionProvider.suggestResource(snapshot.channels().keySet().stream(), builder));
        return builder.buildFuture();
    }
}
