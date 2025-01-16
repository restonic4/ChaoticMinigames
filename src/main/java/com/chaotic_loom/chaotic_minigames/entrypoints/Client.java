package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.core.registries.LaserProjectile;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.incompatibilities.IncompatibilitiesAPI;
import com.chaotic_loom.under_control.api.whitelist.WhitelistAPI;
import com.chaotic_loom.under_control.client.EntityTracker;
import com.chaotic_loom.under_control.client.gui.FatalErrorScreen;
import com.chaotic_loom.under_control.client.rendering.RenderingHelper;
import com.chaotic_loom.under_control.client.rendering.effects.Cube;
import com.chaotic_loom.under_control.client.rendering.effects.CubeManager;
import com.chaotic_loom.under_control.client.rendering.effects.CylinderManager;
import com.chaotic_loom.under_control.events.types.ClientLifeExtraEvents;
import com.chaotic_loom.under_control.networking.services.ApiClient;
import com.chaotic_loom.under_control.networking.services.ApiResponse;
import com.chaotic_loom.under_control.registries.client.UnderControlShaders;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.SynchronizationHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

@Environment(value = EnvType.CLIENT)
public class Client implements ClientModInitializer {
    public static boolean allowed = true;

    public static Cube debugCube = CubeManager.create(-213);

    static {
        debugCube.setScale(new Vector3f(1, 1, 1));
    }

    @Override
    public void onInitializeClient() {
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "essential");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "iris");
        IncompatibilitiesAPI.registerIncompatibleMod(CMSharedConstants.ID, "fancymenu");

        GameManager.getInstanceOrCreate().setSynchronizationHelper(new SynchronizationHelper());

        EntityTracker.trackEntityType(LaserProjectile.class);
        EntityTracker.addConsumer((poseStack, matrix4f, camera, laserProjectile) -> {
            Vec3 position = laserProjectile.position();
            Vec3 velocity = laserProjectile.getDeltaMovement();

            RenderingHelper.renderSphere(poseStack, matrix4f, camera, UnderControlShaders.SIMPLE_COLOR, (float) position.x, (float) position.y, (float) position.z, 1, 1, 1, 0, 0, 0, 0);
            RenderingHelper.renderBillboardQuad(poseStack, matrix4f, camera, (float) position.x, (float) position.y, (float) position.z, (float) (position.x + velocity.x), (float) (position.y + velocity.y), (float) (position.z + velocity.z), 2);
        });

        ClientLifeExtraEvents.CLIENT_STARTED_DELAYED.register((minecraft) -> {
            MusicManager.playMusic(SoundRegistry.MUSIC_MAIN_MENU_1, 4000, EasingSystem.EasingType.LINEAR);
        });

        ClientPlayConnectionEvents.JOIN.register((clientPacketListener, packetSender ,minecraft) -> {
            GameManager.getInstance().getSynchronizationHelper().askForSynchronization();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            GameManager.getInstance().getSynchronizationHelper().clearOffsets();
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
