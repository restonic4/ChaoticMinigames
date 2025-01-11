package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.LaserProjectile;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.under_control.client.rendering.RenderingHelper;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", shift = At.Shift.AFTER))
    public void captureMatrix(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci, @Local Entity entity) {
        if (entity instanceof LaserProjectile laserProjectile) {
            Vec3 position = laserProjectile.position();
            Vec3 velocity = laserProjectile.getDeltaMovement();

            RenderingHelper.renderSphere(poseStack, matrix4f, camera, (float) position.x, (float) position.y, (float) position.z, 1);
            RenderingHelper.renderBillboardQuad(poseStack, matrix4f, camera, (float) position.x, (float) position.y, (float) position.z, (float) (position.x + velocity.x), (float) (position.y + velocity.y), (float) (position.z + velocity.z), 2);
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupNoFog()V", shift = At.Shift.AFTER))
    public void renderLevel(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        for (GenericMinigame genericMinigame : MinigameRegistry.MINIGAMES) {
            genericMinigame.tick(ExecutionSide.CLIENT);
        }
    }
}