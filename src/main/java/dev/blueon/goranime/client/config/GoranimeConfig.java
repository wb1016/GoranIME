package dev.blueon.goranime.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.blueon.goranime.client.GoranimeClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GoranimeConfig {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    private static Path configPath;

    public boolean autoKoreanOnFocus = true;
    public boolean rememberPerScreen = false;
    public boolean preventFullwidth = true;
    public boolean showIndicator = true;

    private static GoranimeConfig instance;

    public static void initialize(Path configDir) {
        configPath = configDir.resolve("goranime.json");
        instance = load();
    }

    public static GoranimeConfig get() {
        if (instance == null) instance = new GoranimeConfig();
        return instance;
    }

    public static void save() {
        if (configPath == null || instance == null) return;
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(instance));
        } catch (IOException e) {
            GoranimeClient.LOGGER.error("Failed to save config", e);
        }
    }

    private static GoranimeConfig load() {
        if (configPath == null) return new GoranimeConfig();
        try {
            if (Files.exists(configPath)) {
                return GSON.fromJson(
                    Files.readString(configPath),
                    GoranimeConfig.class
                );
            }
        } catch (IOException | JsonParseException e) {
            GoranimeClient.LOGGER.warn(
                "Failed to load config, using defaults",
                e
            );
        }
        GoranimeConfig def = new GoranimeConfig();
        def.saveConfig();
        return def;
    }

    private void saveConfig() {
        save();
    }
}
