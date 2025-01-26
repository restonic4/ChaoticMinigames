package com.chaotic_loom.chaotic_minigames.mixin.common;

import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.KnockEmOff;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTravel(Vec3 movementInput, CallbackInfo ci) {
        Player self = (Player) (Object) this;

        if (self.level().isClientSide()
                && self == Minecraft.getInstance().player
                && KnockEmOff.getLocalPointA() != null
                && KnockEmOff.getLocalPointB() != null) {

            Vector3f a = KnockEmOff.getLocalPointA();
            Vector3f b = KnockEmOff.getLocalPointB();
            Vec3 aVec = new Vec3(a.x(), a.y(), a.z());
            Vec3 bVec = new Vec3(b.x(), b.y(), b.z());

            Vec3 direction = bVec.subtract(aVec).normalize();

            float xxa = (float) movementInput.x;

            float speed = self.getSpeed();
            Vec3 movement = direction.scale(xxa * speed);

            self.setDeltaMovement(
                    self.getDeltaMovement().x + movement.x,
                    self.getDeltaMovement().y,
                    self.getDeltaMovement().z + movement.z
            );

            ci.cancel();
        }
    }
}
