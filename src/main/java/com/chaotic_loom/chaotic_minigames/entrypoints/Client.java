package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.core.registries.RegistriesManager;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.PacketManager;
import com.chaotic_loom.under_control.api.incompatibilities.IncompatibilitiesAPI;
import com.chaotic_loom.under_control.events.types.ClientLifeExtraEvents;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RegistriesManager.registerClient();
        PacketManager.registerServerToClient();

        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "essential");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "iris");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "fancymenu");

        ClientLifeExtraEvents.CLIENT_STARTED_DELAYED.register((minecraft) -> {
            MusicManager.playMusic(SoundRegistry.MUSIC_MAIN_MENU_1, 4000, EasingSystem.EasingType.LINEAR);
        });
    }

    public static boolean areKeysPressed(Minecraft client, int... keys) {
        for (int key : keys) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key)) {
                return false;
            }
        }
        return true;
    }
}
