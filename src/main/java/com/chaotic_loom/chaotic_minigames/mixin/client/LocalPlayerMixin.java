package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.KnockEmOff;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow public Input input;

    @Inject(
            method = "serverAiStep",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;zza:F",
                    shift = At.Shift.AFTER
            )
    )
    public void serverAiStep(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;

        if (isFrozen()) {
            self.xxa = 0;
            self.zza = 0;

            self.setDeltaMovement(0, 0, 0);
        }

        self.zza = KnockEmOff.getLocalPointA() != null && KnockEmOff.getLocalPointB() != null
                ? 0.0F
                : this.input.forwardImpulse;
    }

    @Unique
    private boolean isFrozen() {
        return KnownServerDataOnClient.frozenPosition != null;
    }
}
