package com.chaotic_loom.chaotic_minigames.mixin.client.music;

import com.chaotic_loom.chaotic_minigames.mixin_extra.SoundEngineExtra;
import com.chaotic_loom.chaotic_minigames.mixin_extra.SoundManagerExtra;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundManager.class)
public class SoundManagerMixin implements SoundManagerExtra {
    @Shadow @Final private SoundEngine soundEngine;

    @Unique
    public void chaoticMinigames$setVolume(SoundInstance soundInstance, float f) {
        ((SoundEngineExtra) this.soundEngine).chaoticMinigames$setVolume(soundInstance, f);
    }

    @WrapOperation(
            method = "updateSourceVolume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/SoundManager;stop()V"
            )
    )
    public void cancelChannelRemoval(SoundManager instance, Operation<Void> original) {
        //cancelled
    }
}
