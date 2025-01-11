package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.config.ConfigProvider;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.telemetry.TelemetryProperty;

public class DedicatedServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(GameManager::onStart);


        final boolean[] isPartyServer = {false};
        final boolean[] checked = {false};

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            if (!checked[0]) {
                checked[0] = true;

                ConfigProvider configProvider = ConfigAPI.getServerProvider(CMSharedConstants.ID);
                isPartyServer[0] = ServerStatus.Type.valueOf(configProvider.get("server_type", String.class)) == ServerStatus.Type.PARTY;
            }

            if (isPartyServer[0]) {
                for (GenericMinigame genericMinigame : MinigameRegistry.MINIGAMES) {
                    if (genericMinigame.canTickOnServer()) {
                        genericMinigame.tick(ExecutionSide.SERVER);
                    }
                }
            }
        });
    }
}
