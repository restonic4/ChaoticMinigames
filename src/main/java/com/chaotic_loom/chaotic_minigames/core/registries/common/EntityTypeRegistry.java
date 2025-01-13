package com.chaotic_loom.chaotic_minigames.core.registries.common;

import com.chaotic_loom.chaotic_minigames.core.registries.LaserProjectile;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.core.annotations.Registration;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@Registration(side = ExecutionSide.COMMON)
public class EntityTypeRegistry {
    public static final EntityType<LaserProjectile> LASER_PROJECTILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(CMSharedConstants.ID, "lase_projectile"),
            FabricEntityTypeBuilder.<LaserProjectile>create(MobCategory.MISC, LaserProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                    .trackRangeBlocks(100)
                    .trackedUpdateRate(3)
                    .forceTrackedVelocityUpdates(true)
                    .fireImmune()
                    .build()
    );

    public static void register() {

    }
}
