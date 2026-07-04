package dev.blueon.goranime.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class GoranimeKeyBinds {

    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
        Identifier.fromNamespaceAndPath("goranime", "keybinds")
    );

    private static KeyMapping langToggle;

    private GoranimeKeyBinds() {}

    public static void register() {
        langToggle = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                "key.goranime.toggle_language",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT,
                CATEGORY
            )
        );
    }

    public static KeyMapping langToggle() {
        return langToggle;
    }
}
