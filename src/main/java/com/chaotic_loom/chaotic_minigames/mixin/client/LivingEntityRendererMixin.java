package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow protected abstract float getBob(T livingEntity, float f);

    @Shadow protected M model;

    @Redirect(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            )
    )
    public void render(EntityModel instance, PoseStack poseStack, VertexConsumer vertexConsumer, int i, int g, float v, float v2, float v3, float v4, @Local(argsOnly = true) T livingEntity, @Local(argsOnly = true) MultiBufferSource multiBufferSource) {
        if (livingEntity instanceof Player && isZombie(livingEntity)) {
            RenderType renderType = CMClientConstants.ZOMBIE_MODEL.renderType(CMClientConstants.ZOMBIE_TEXTURE);
            VertexConsumer vertexConsumerFixed = multiBufferSource.getBuffer(renderType);

            CMClientConstants.ZOMBIE_MODEL.young = false;
            Minecraft.getInstance().getTextureManager().bindForSetup(CMClientConstants.ZOMBIE_TEXTURE);

            AbstractZombieModel zombieModel = (AbstractZombieModel) CMClientConstants.ZOMBIE_MODEL;
            HumanoidModel humanoidModel = (HumanoidModel) this.model;

            zombieModel.leftArm.setRotation(humanoidModel.leftArm.xRot, humanoidModel.leftArm.yRot, humanoidModel.leftArm.zRot);
            zombieModel.rightArm.setRotation(humanoidModel.rightArm.xRot, humanoidModel.rightArm.yRot, humanoidModel.rightArm.zRot);

            zombieModel.leftLeg.setRotation(humanoidModel.leftLeg.xRot, humanoidModel.leftLeg.yRot, humanoidModel.leftLeg.zRot);
            zombieModel.rightLeg.setRotation(humanoidModel.rightLeg.xRot, humanoidModel.rightLeg.yRot, humanoidModel.rightLeg.zRot);

            zombieModel.head.setRotation(humanoidModel.head.xRot, humanoidModel.head.yRot, humanoidModel.head.zRot);
            zombieModel.body.setRotation(humanoidModel.body.xRot, humanoidModel.body.yRot, humanoidModel.body.zRot);

            zombieModel.leftArm.setPos(humanoidModel.leftArm.x, humanoidModel.leftArm.y, humanoidModel.leftArm.z);
            zombieModel.rightArm.setPos(humanoidModel.rightArm.x, humanoidModel.rightArm.y, humanoidModel.rightArm.z);

            zombieModel.leftLeg.setPos(humanoidModel.leftLeg.x, humanoidModel.leftLeg.y, humanoidModel.leftLeg.z);
            zombieModel.rightLeg.setPos(humanoidModel.rightLeg.x, humanoidModel.rightLeg.y, humanoidModel.rightLeg.z);

            zombieModel.head.setPos(humanoidModel.head.x, humanoidModel.head.y, humanoidModel.head.z);
            zombieModel.body.setPos(humanoidModel.body.x, humanoidModel.body.y, humanoidModel.body.z);

            AnimationUtils.animateZombieArms(zombieModel.leftArm, zombieModel.rightArm, true, zombieModel.attackTime, this.getBob(livingEntity, g));

            CMClientConstants.ZOMBIE_MODEL.renderToBuffer(poseStack, vertexConsumerFixed, i, g, v, v2, v3, v4);
        } else {
            instance.renderToBuffer(poseStack, vertexConsumer, i, g, v, v2, v3, v4);
        }
    }

    /*@WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V"
            )
    )
    public void model(EntityModel instance, Entity entity, float f, float g, float h, Operation<Void> original, @Local(argsOnly = true) T livingEntity) {
        if (livingEntity instanceof Player && isZombie(livingEntity)) {
            CMClientConstants.ZOMBIE_MODEL.prepareMobModel(entity, f, g, h);
        } else {
            original.call(instance, entity, f, g, h);
        }
    }*/

    /*@WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
            )
    )
    public void animate(EntityModel instance, Entity livingEntity, float o, float n, float l, float k, float m, Operation<Void> original) {
        if (livingEntity instanceof Player && isZombie(livingEntity)) {
            original.call(instance, livingEntity, o, n, l, k, m);

            AbstractZombieModel zombieModel = (AbstractZombieModel) CMClientConstants.ZOMBIE_MODEL;
            AnimationUtils.animateZombieArms(zombieModel.leftArm, zombieModel.rightArm, true, zombieModel.attackTime, l);
        } else {
            original.call(instance, livingEntity, o, n, l, k, m);
        }
    }*/

    @Unique
    private boolean isZombie(Entity livingEntity) {
        if (livingEntity instanceof Player player) {
            for (String playerFound : KnownServerDataOnClient.zombiePlayersUUIDs) {
                if (playerFound.equals(player.getGameProfile().getId().toString())) {
                    return true;
                }
            }
        }

        return false;
    }
}

