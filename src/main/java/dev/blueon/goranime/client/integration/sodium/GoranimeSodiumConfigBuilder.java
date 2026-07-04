package dev.blueon.goranime.client.integration.sodium;

import dev.blueon.goranime.client.GoranimeClient;
import dev.blueon.goranime.client.config.GoranimeConfig;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class GoranimeSodiumConfigBuilder implements ConfigEntryPoint {

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        ModOptionsBuilder options = builder.registerModOptions(
            GoranimeClient.MODID
        );
        options.setIcon(GoranimeClient.id("config-icon.png"));
        options.addPage(
            builder
                .createOptionPage()
                .setName(Component.translatable("goranime.config.page.input"))
                .addOption(
                    builder
                        .createBooleanOption(
                            GoranimeClient.id("prevent_fullwidth")
                        )
                        .setName(
                            Component.translatable(
                                "goranime.config.option.prevent_fullwidth"
                            )
                        )
                        .setTooltip(
                            Component.translatable(
                                "goranime.config.option.prevent_fullwidth.tooltip"
                            )
                        )
                        .setEnabled(Util.getPlatform() == Util.OS.WINDOWS)
                        .setControlHiddenWhenDisabled(false)
                        .setDefaultValue(true)
                        .setBinding(
                            value ->
                                GoranimeConfig.get().preventFullwidth = value,
                            () -> GoranimeConfig.get().preventFullwidth
                        )
                        .setStorageHandler(GoranimeConfig::save)
                )
                .addOption(
                    builder
                        .createBooleanOption(
                            GoranimeClient.id("auto_korean_on_focus")
                        )
                        .setName(
                            Component.translatable(
                                "goranime.config.option.auto_korean_on_focus"
                            )
                        )
                        .setTooltip(
                            Component.translatable(
                                "goranime.config.option.auto_korean_on_focus.tooltip"
                            )
                        )
                        .setDefaultValue(true)
                        .setBinding(
                            value ->
                                GoranimeConfig.get().autoKoreanOnFocus = value,
                            () -> GoranimeConfig.get().autoKoreanOnFocus
                        )
                        .setStorageHandler(GoranimeConfig::save)
                )
        );
    }
}
