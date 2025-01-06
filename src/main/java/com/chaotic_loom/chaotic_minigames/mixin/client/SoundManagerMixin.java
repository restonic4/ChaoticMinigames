package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @WrapOperation(
            method = "updateSourceVolume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/SoundManager;stop()V"
            )
    )
    public void updateSourceVolume(SoundManager instance, Operation<Void> original) {
        //cancelled
    }
}
