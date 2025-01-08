package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.mixin_extra.ExtraSoundSourcesHolder;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
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
    public static Component genericValueLabel(Component component, int integer) {
        return null;
    }

    @Shadow
    private static Component percentValueLabel(Component component, double d) {
        return null;
    }

    @Final @Shadow private OptionInstance<Integer> fov = new OptionInstance<>(
            "options.fov",
            OptionInstance.noTooltip(),
            (component, integer) -> {
                return switch(integer) {
                    case 70 -> genericValueLabel(component, Component.translatable("options.fov.min"));
                    case 90 -> genericValueLabel(component, Component.translatable("options.fov.real"));
                    case 110 -> genericValueLabel(component, Component.translatable("options.fov.max"));
                    default -> genericValueLabel(component, integer);
                };
            },
            new OptionInstance.IntRange(30, 110),
            Codec.DOUBLE.xmap(double_ -> (int)(double_ * 40.0 + 70.0), integer -> ((double)integer.intValue() - 70.0) / 40.0),
            90,
            integer -> Minecraft.getInstance().levelRenderer.needsUpdate()
    );

    @Final @Shadow private OptionInstance<Double> gamma = new OptionInstance<>(
            "options.gamma",
            OptionInstance.noTooltip(),
            (component, double_) -> {
                int i = (int) (double_ * 100.0);
                if (i == 0) {
                    return genericValueLabel(component, Component.translatable("options.gamma.min"));
                } else if (i == 50) {
                    return genericValueLabel(component, Component.translatable("options.gamma.default"));
                } else {
                    return i == 100 ? genericValueLabel(component, Component.translatable("options.gamma.max")) : genericValueLabel(component, i);
                }
            }, OptionInstance.UnitDouble.INSTANCE,
            1d,
            double_ -> {}
    );

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

        if (soundSource == ExtraSoundSourcesHolder.SOUND_EFFECTS) {
            defaultValue = 0.5;
            tooltip = Tooltip.create(Component.translatable("gui.chaotic_minigames.options.sound_effects_slider.tooltip"));
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
