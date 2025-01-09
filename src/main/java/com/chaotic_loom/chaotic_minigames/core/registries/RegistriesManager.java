package com.chaotic_loom.chaotic_minigames.core.registries;


import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.core.registries.server.MinigameRegistry;

public class RegistriesManager {
    public static void registerCommon() {
        SoundRegistry.register();
    }

    public static void registerClient() {

    }

    public static void registerServer() {
        MinigameRegistry.register();
    }
}
