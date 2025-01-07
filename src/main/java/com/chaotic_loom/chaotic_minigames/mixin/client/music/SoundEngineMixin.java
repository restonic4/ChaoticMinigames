package com.chaotic_loom.chaotic_minigames.mixin.client.music;

import com.chaotic_loom.chaotic_minigames.mixin_extra.SoundEngineExtra;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEngineExecutor;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements SoundEngineExtra {
    @Shadow protected abstract float calculateVolume(SoundInstance soundInstance);

    @Shadow private boolean loaded;

    @Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow @Final private SoundEngineExecutor executor;

    @Shadow @Final private ChannelAccess channelAccess;

    @Shadow @Final private Map<SoundInstance, Integer> queuedSounds;

    @Shadow @Final private List<TickableSoundInstance> tickingSounds;

    @Shadow @Final private Multimap<SoundSource, SoundInstance> instanceBySource;

    @Shadow @Final private Map<SoundInstance, Integer> soundDeleteTime;

    @Shadow @Final private List<TickableSoundInstance> queuedTickableSounds;

    @Shadow private int tickCount;

    @Override
    public void chaoticMinigames$setVolume(SoundInstance soundInstance, float f) {
        if (this.loaded) {
            ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(channel -> channel.setVolume(f * this.calculateVolume(soundInstance)));
            }
        }
    }

    /**
     * @author restonic4
     * @reason I am dumb and I could not figure out how to properly remove this.instanceToChannel.values().forEach(channelHandle -> channelHandle.execute(Channel::stop));
     */
    @Overwrite
    public void stopAll() {
        if (this.loaded) {
            this.executor.flush();

            Map<Integer, SoundSource> channelIdToSoundSource = new HashMap<>();

            // Prevent Music channel being obliterated
            for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry : this.instanceToChannel.entrySet()) {
                SoundInstance soundInstance = entry.getKey();
                ChannelAccess.ChannelHandle channelHandle = entry.getValue();

                if (channelHandle.channel != null) {
                    channelIdToSoundSource.put(channelHandle.channel.source, soundInstance.getSource());
                }

                if (soundInstance.getSource() != SoundSource.MUSIC && soundInstance.getSource() != SoundSource.MASTER) {
                    channelHandle.execute(Channel::stop);
                }
            }

            Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> instanceToChannelIterator = this.instanceToChannel.entrySet().iterator();
            while (instanceToChannelIterator.hasNext()) {
                Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry = instanceToChannelIterator.next();
                SoundInstance soundInstance = entry.getKey();
                ChannelAccess.ChannelHandle channelHandle = entry.getValue();

                if (soundInstance.getSource() != SoundSource.MUSIC && soundInstance.getSource() != SoundSource.MASTER) {
                    instanceToChannelIterator.remove();
                }
            }

            Iterator<ChannelAccess.ChannelHandle> iterator = channelAccess.channels.iterator();
            while (iterator.hasNext()) {
                ChannelAccess.ChannelHandle channelHandle = iterator.next();

                if (channelHandle.channel != null) {
                    SoundSource soundSource = channelIdToSoundSource.get(channelHandle.channel.source);

                    if (soundSource != SoundSource.MUSIC && soundSource != SoundSource.MASTER) {
                        channelHandle.release();
                        iterator.remove();
                    }
                }
            }

            this.queuedSounds.clear();
            this.tickingSounds.clear();
            this.instanceBySource.clear();
            this.soundDeleteTime.clear();
            this.queuedTickableSounds.clear();
        }
    }

    @Redirect(
            method = "tickNonPaused",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private Object redirectSoundDeleteTimeGet(Map<SoundInstance, Integer> instance, Object key) {
        SoundInstance soundInstance = (SoundInstance) key;

        if (soundInstance != null && (soundInstance.getSource() == SoundSource.MUSIC || soundInstance.getSource() == SoundSource.MASTER)) {
            return this.tickCount + 1;
        }

        return instance.get(soundInstance);
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

        if (soundSource != SoundSource.MUSIC && soundSource != SoundSource.MASTER) {
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

        if (soundSource != SoundSource.MUSIC && soundSource != SoundSource.MASTER) {
            original.call(instance);
        }
    }

    @Inject(method = "stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), cancellable = true)
    public void stop(SoundInstance soundInstance, CallbackInfo ci)  {
        if (soundInstance.getSource() == SoundSource.MUSIC || soundInstance.getSource() == SoundSource.MASTER) {
            ci.cancel();
        }
    }
}
