package com.chaotic_loom.chaotic_minigames.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(Options.class)
public abstract class OptionsMixins {
    @Shadow
    public static Component genericValueLabel(Component component, Component component2) {
        return null;
    }

    @Shadow
    private static Component percentValueLabel(Component component, double d) {
        return null;
    }

    @Inject(method = "createSoundSliderOptionInstance", at = @At("HEAD"), cancellable = true)
    private void modifyDefaultSoundSlider(String key, SoundSource soundSource, CallbackInfoReturnable<OptionInstance<Double>> cir) {
        double defaultValue = 1.0;
        Tooltip tooltip = null;

        switch (soundSource) {
            case MUSIC:
                defaultValue = 0.10;
                tooltip = Tooltip.create(Component.translatable("gui.chaotic_minigames.options.music_slider.tooltip"));
                break;
            case RECORDS, WEATHER, AMBIENT:
                defaultValue = 0.10;
                break;
            case HOSTILE, NEUTRAL:
                defaultValue = 0.5;
                break;
        }

        Tooltip finalTooltip = tooltip;
        OptionInstance<Double> customOptionInstance = new OptionInstance<>(
                key,
                (tooltip != null ? object -> finalTooltip : OptionInstance.noTooltip()),
                (component, value) -> (Double) value == 0.0 ? genericValueLabel(component, CommonComponents.OPTION_OFF) : percentValueLabel(component, (Double) value),
                OptionInstance.UnitDouble.INSTANCE,
                defaultValue,
                value -> Minecraft.getInstance().getSoundManager().updateSourceVolume(soundSource, value.floatValue())
        );

        cir.setReturnValue(customOptionInstance);
    }
}
