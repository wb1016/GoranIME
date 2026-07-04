package dev.blueon.goranime.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.blueon.goranime.client.config.GoranimeConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class GoranimeModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> createConfigScreen(parent);
    }

    private static Screen createConfigScreen(Screen parent) {
        GoranimeConfig defaults = new GoranimeConfig();
        GoranimeConfig config = GoranimeConfig.get();

        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("GoranIME"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.literal("Input"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.literal("Composition"))
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(
                                        Component.literal(
                                            "Auto Korean on Focus"
                                        )
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.literal(
                                                "Automatically enable Korean input when a text field gains focus."
                                            )
                                        )
                                    )
                                    .binding(
                                        defaults.autoKoreanOnFocus,
                                        () -> config.autoKoreanOnFocus,
                                        v -> config.autoKoreanOnFocus = v
                                    )
                                    .controller(
                                        BooleanControllerBuilder::create
                                    )
                                    .build()
                            )
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(
                                        Component.literal("Remember per Screen")
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.literal(
                                                "Remember Korean/English state separately for each screen type."
                                            )
                                        )
                                    )
                                    .binding(
                                        defaults.rememberPerScreen,
                                        () -> config.rememberPerScreen,
                                        v -> config.rememberPerScreen = v
                                    )
                                    .controller(
                                        BooleanControllerBuilder::create
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.literal("Indicator"))
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(Component.literal("Show Indicator"))
                                    .description(
                                        OptionDescription.of(
                                            Component.literal(
                                                "Show KO badge inside text fields when Korean mode is active."
                                            )
                                        )
                                    )
                                    .binding(
                                        defaults.showIndicator,
                                        () -> config.showIndicator,
                                        v -> config.showIndicator = v
                                    )
                                    .controller(
                                        BooleanControllerBuilder::create
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.literal("Windows"))
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(
                                        Component.literal(
                                            "Prevent Fullwidth Switching"
                                        )
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.literal(
                                                "On Windows, force IME to stay in halfwidth mode."
                                            )
                                        )
                                    )
                                    .binding(
                                        defaults.preventFullwidth,
                                        () -> config.preventFullwidth,
                                        v -> config.preventFullwidth = v
                                    )
                                    .controller(
                                        BooleanControllerBuilder::create
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .save(GoranimeConfig::save)
            .build()
            .generateScreen(parent);
    }
}
