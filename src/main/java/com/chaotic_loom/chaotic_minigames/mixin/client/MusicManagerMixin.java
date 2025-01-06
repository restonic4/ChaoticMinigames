package com.chaotic_loom.chaotic_minigames.mixin.client;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        com.chaotic_loom.chaotic_minigames.core.MusicManager.tick();
        ci.cancel();
    }
}
