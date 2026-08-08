package cc.yamrc.konechat.platform;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ServerTranslations {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final Map<String, Map<String, String>> TRANSLATIONS = Map.of(
            DEFAULT_LANGUAGE, load(DEFAULT_LANGUAGE),
            "zh_cn", load("zh_cn")
    );

    private ServerTranslations() {
    }

    public static MutableComponent message(CommandSourceStack source, String key, Object... arguments) {
        return message(source.getPlayer(), key, arguments);
    }

    public static MutableComponent message(@Nullable ServerPlayer player, String key, Object... arguments) {
        String language = player == null ? DEFAULT_LANGUAGE
                : player.getLanguage().toLowerCase(Locale.ROOT);
        Map<String, String> localized = TRANSLATIONS.getOrDefault(language, TRANSLATIONS.get(DEFAULT_LANGUAGE));
        String fallback = localized.get(key);
        if (fallback == null) fallback = TRANSLATIONS.get(DEFAULT_LANGUAGE).get(key);
        if (fallback == null) {
            LOGGER.warn("Missing KoneChat server translation for {}", key);
            fallback = key;
        }
        return Component.translatableWithFallback(key, fallback, arguments);
    }

    private static Map<String, String> load(String language) {
        String path = "/assets/konechat/lang/" + language + ".json";
        try (InputStream stream = ServerTranslations.class.getResourceAsStream(path)) {
            if (stream == null) throw new IllegalStateException("Missing language resource " + path);
            Map<String, String> translations = new HashMap<>();
            Language.loadFromJson(stream, translations::put);
            return Map.copyOf(translations);
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Failed to load KoneChat server translations from {}", path, exception);
            return Map.of();
        }
    }
}
