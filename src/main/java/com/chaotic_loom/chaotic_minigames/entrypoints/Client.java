package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.incompatibilities.IncompatibilitiesAPI;
import com.chaotic_loom.under_control.events.types.ClientLifeExtraEvents;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.SynchronizationHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class Client implements ClientModInitializer {
    public static final SynchronizationHelper synchronizationHelper = new SynchronizationHelper(6);

    @Override
    public void onInitializeClient() {
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "essential");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "iris");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "fancymenu");

        ClientLifeExtraEvents.CLIENT_STARTED_DELAYED.register((minecraft) -> {
            MusicManager.playMusic(SoundRegistry.MUSIC_MAIN_MENU_1, 4000, EasingSystem.EasingType.LINEAR);
        });

        ClientPlayConnectionEvents.JOIN.register((clientPacketListener, packetSender ,minecraft) -> {
            synchronizationHelper.askForSynchronization();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            synchronizationHelper.clearOffsets();
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
