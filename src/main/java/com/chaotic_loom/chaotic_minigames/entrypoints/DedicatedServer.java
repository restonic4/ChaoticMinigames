package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.registries.RegistriesManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class DedicatedServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        RegistriesManager.registerServer();
        ServerLifecycleEvents.SERVER_STARTED.register(GameManager::onStart);
    }
}
