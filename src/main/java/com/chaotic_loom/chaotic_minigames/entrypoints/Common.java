package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.registries.RegistriesManager;
import com.chaotic_loom.chaotic_minigames.networking.PacketManager;
import net.fabricmc.api.ModInitializer;

public class Common implements ModInitializer {
    @Override
    public void onInitialize() {
        RegistriesManager.registerCommon();
        PacketManager.registerClientToServer();
    }
}
