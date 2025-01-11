package com.chaotic_loom.chaotic_minigames.core.registries.client;

import com.chaotic_loom.chaotic_minigames.core.registries.LaserProjectileRenderer;
import com.chaotic_loom.chaotic_minigames.core.registries.common.EntityTypeRegistry;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.core.annotations.Registration;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

@Registration(side = ExecutionSide.CLIENT)
public class EntityRenderersRegistry {
    public static void register() {
        EntityRendererRegistry.register(EntityTypeRegistry.LASER_PROJECTILE, LaserProjectileRenderer::new);
    }
}
