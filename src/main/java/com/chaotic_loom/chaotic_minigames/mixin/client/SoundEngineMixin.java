package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Shadow protected abstract float calculateVolume(SoundInstance soundInstance);

    @Shadow protected abstract float getVolume(@Nullable SoundSource soundSource);

    @WrapOperation(
            method = "updateCategoryVolume",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"
            )
    )
    public void updateCategoryVolume(Map<SoundInstance, ChannelAccess.ChannelHandle> instance, BiConsumer<?, ?> v, Operation<Void> original) {
        instance.forEach((soundInstance, channelHandle) -> {
            float fx = this.calculateVolume(soundInstance);

            channelHandle.execute(channel -> {
                channel.setVolume(fx);
            });
        });
    }

    @Redirect(
            method = "getVolume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;getSoundSourceVolume(Lnet/minecraft/sounds/SoundSource;)F"
            )
    )
    private float getVolume(Options instance, SoundSource soundSource) {
        if (soundSource == SoundSource.MUSIC) {
            return instance.getSoundSourceVolume(soundSource) * MusicManager.getCurrentVolume();
        }

        return instance.getSoundSourceVolume(soundSource);
    }

    @WrapOperation(
            method = "tickNonPaused",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V",
                    ordinal = 1
            )
    )
    private void preventMusicChannelStop1(ChannelAccess.ChannelHandle instance, Consumer<Channel> consumer, Operation<Void> original, @Local SoundInstance soundInstance) {
        SoundSource soundSource = soundInstance.getSource();

        if (soundSource != SoundSource.MUSIC) {
            original.call(instance, consumer);
        }
    }

    @WrapOperation(
            method = "tickNonPaused",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;remove()V",
                    ordinal = 0
            )
    )
    private void preventMusicChannelStop2(Iterator<?> instance, Operation<Void> original, @Local SoundInstance soundInstance) {
        SoundSource soundSource = soundInstance.getSource();

        if (soundSource != SoundSource.MUSIC) {
            original.call(instance);
        }
    }
}
