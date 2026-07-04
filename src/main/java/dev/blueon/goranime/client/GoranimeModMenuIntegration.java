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
            .title(Component.translatable("goranime.config.title"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("goranime.config.page.input"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(
                                Component.translatable(
                                    "goranime.config.group.composition"
                                )
                            )
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(
                                        Component.translatable(
                                            "goranime.config.option.auto_korean_on_focus"
                                        )
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.translatable(
                                                "goranime.config.option.auto_korean_on_focus.tooltip"
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
                                        Component.translatable(
                                            "goranime.config.option.remember_per_screen"
                                        )
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.translatable(
                                                "goranime.config.option.remember_per_screen.tooltip"
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
                            .name(
                                Component.translatable(
                                    "goranime.config.group.indicator"
                                )
                            )
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(
                                        Component.translatable(
                                            "goranime.config.option.show_indicator"
                                        )
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.translatable(
                                                "goranime.config.option.show_indicator.tooltip"
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
                            .name(
                                Component.translatable(
                                    "goranime.config.group.windows"
                                )
                            )
                            .option(
                                Option.<Boolean>createBuilder()
                                    .name(
                                        Component.translatable(
                                            "goranime.config.option.prevent_fullwidth"
                                        )
                                    )
                                    .description(
                                        OptionDescription.of(
                                            Component.translatable(
                                                "goranime.config.option.prevent_fullwidth.tooltip"
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
