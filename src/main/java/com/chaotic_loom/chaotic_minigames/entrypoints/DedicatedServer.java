package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class DedicatedServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(GameManager::onStart);

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            for (GenericMinigame genericMinigame : MinigameRegistry.MINIGAMES) {
                genericMinigame.tick(ExecutionSide.SERVER);
            }
        });
    }
}
