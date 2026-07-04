package dev.blueon.goranime.client;

import dev.blueon.goranime.client.config.GoranimeConfig;
import dev.blueon.goranime.client.ime.LangTypeManager;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class GoranimeClient {

    public static final String MODID = "goranime";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private GoranimeClient() {}

    public static void initializeConfig(Path configDir) {
        GoranimeConfig.initialize(configDir);
    }

    public static void initializeKeyBinds() {
        GoranimeKeyBinds.register();
    }

    public static void onClientStarted() {
        LangTypeManager.get().setKorean(false);
        LOGGER.info("GoranIME loaded");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
