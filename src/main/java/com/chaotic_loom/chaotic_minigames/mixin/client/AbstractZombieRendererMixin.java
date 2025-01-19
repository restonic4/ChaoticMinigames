package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractZombieRenderer.class)
public class AbstractZombieRendererMixin {
    @Shadow @Final private static ResourceLocation ZOMBIE_LOCATION;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityRendererProvider.Context context, ZombieModel zombieModel, ZombieModel zombieModel2, ZombieModel zombieModel3, CallbackInfo ci) {
        CMClientConstants.ZOMBIE_MODEL = zombieModel;
        CMClientConstants.ZOMBIE_TEXTURE = ZOMBIE_LOCATION;
    }
}
