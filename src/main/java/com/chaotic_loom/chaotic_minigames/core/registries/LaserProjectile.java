package com.chaotic_loom.chaotic_minigames.core.registries;

import com.chaotic_loom.chaotic_minigames.core.registries.common.EntityTypeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class LaserProjectile extends Projectile {
    public LaserProjectile(EntityType<? extends Projectile> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    public LaserProjectile(Level level, LivingEntity livingEntity) {
        super(EntityTypeRegistry.LASER_PROJECTILE, level);
        this.setPos(livingEntity.getX(), livingEntity.getEyeY() - (double)0.1F, livingEntity.getZ());
        setOwner(livingEntity);
    }

    @Override
    protected void defineSynchedData() {}
}
