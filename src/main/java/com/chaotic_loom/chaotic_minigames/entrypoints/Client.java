package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.core.registries.RegistriesManager;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.PacketManager;
import com.chaotic_loom.under_control.api.incompatibilities.IncompatibilitiesAPI;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.sounds.SoundEvents;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RegistriesManager.registerClient();
        PacketManager.registerServerToClient();

        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "essential");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "iris");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "fancymenu");

        ClientLifecycleEvents.CLIENT_STARTED.register((minecraft) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(6000);
                } catch (Exception ignored) {}

                MusicManager.playMusic(SoundEvents.MUSIC_DISC_OTHERSIDE, 4, EasingSystem.EasingType.LINEAR);

                try {
                    Thread.sleep(6000);
                } catch (Exception ignored) {}

                MusicManager.playMusic(SoundEvents.MUSIC_DISC_CAT, 2, EasingSystem.EasingType.LINEAR);

                try {
                    Thread.sleep(6000);
                } catch (Exception ignored) {}

                MusicManager.playMusic(SoundEvents.MUSIC_DISC_CAT, 6, EasingSystem.EasingType.LINEAR);
            }).start();
        });
    }
}
