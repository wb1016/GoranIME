package dev.blueon.goranime.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class GoranimeFabricClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GoranimeClient.initializeConfig(
            FabricLoader.getInstance().getConfigDir()
        );
        GoranimeClient.initializeKeyBinds();
        GoranimeClient.onClientStarted();
    }
}
