package com.chaotic_loom.chaotic_minigames.mixin.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @Shadow private int foodLevel;

    @Shadow private float saturationLevel;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(Player player, CallbackInfo ci) {
        this.foodLevel = 20;
        this.saturationLevel = 5;
    }
}
