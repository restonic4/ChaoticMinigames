package com.chaotic_loom.chaotic_minigames.core.registries;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LaserProjectileRenderer extends EntityRenderer<LaserProjectile> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");

    public LaserProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LaserProjectile entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(LaserProjectile entity) {
        return TEXTURE_LOCATION;
    }
}
