package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.minigames.BulletChaos;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.RegistriesManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.reflections.Reflections;

import java.util.Set;

public class DedicatedServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        RegistriesManager.registerServer();
        ServerLifecycleEvents.SERVER_STARTED.register(GameManager::onStart);

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            BulletChaos.tick();
        });
    }
}
