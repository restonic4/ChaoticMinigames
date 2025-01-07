package com.chaotic_loom.chaotic_minigames.mixin.client.music;

import com.chaotic_loom.chaotic_minigames.mixin_extra.SoundEngineExtra;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements SoundEngineExtra {
    @Shadow protected abstract float calculateVolume(SoundInstance soundInstance);

    @Shadow private boolean loaded;

    @Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Override
    public void chaoticMinigames$setVolume(SoundInstance soundInstance, float f) {
        if (this.loaded) {
            ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(channel -> channel.setVolume(f * this.calculateVolume(soundInstance)));
            }
        }
    }

    @WrapOperation(
            method = "updateCategoryVolume",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"
            )
    )
    public void preventChannelRemoval(Map<SoundInstance, ChannelAccess.ChannelHandle> instance, BiConsumer<?, ?> v, Operation<Void> original) {
        instance.forEach((soundInstance, channelHandle) -> {
            float fx = this.calculateVolume(soundInstance);

            channelHandle.execute(channel -> {
                channel.setVolume(fx);
            });
        });
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
