package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(
            method = "serverAiStep",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;zza:F",
                    shift = At.Shift.AFTER
            )
    )
    public void serverAiStep(CallbackInfo ci) {
        if (isFrozen()) {
            LocalPlayer self = (LocalPlayer) (Object) this;

            self.xxa = 0;
            self.zza = 0;

            self.setDeltaMovement(0, 0, 0);
        }
    }

    @Unique
    private boolean isFrozen() {
        return KnownServerDataOnClient.frozenPosition != null;
    }
}
