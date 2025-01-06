package com.chaotic_loom.chaotic_minigames.mixin.client;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AccessibilityOnboardingScreen.class)
public class AccessibilityOnboardingScreenMixin {
    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;")
    )
    private static MutableComponent redirectTitle(String key) {
        return Component.translatable("gui.chaotic_minigames.onboarding_screen.title");
    }
}
