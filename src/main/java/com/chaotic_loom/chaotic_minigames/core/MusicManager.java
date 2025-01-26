package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.mixin_extra.SoundManagerExtra;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MusicManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static List<SoundInstance> fadingOutMusics = new ArrayList<>();
    private static SoundInstance currentMusic;
    private static SoundInstance nextMusic;

    private static long fadeStartTime = -1;
    private static long fadeEndTime = -1;

    private static double currentVolume = 1.0;

    private static EasingSystem.EasingType currentFadeType = EasingSystem.EasingType.LINEAR;

    private static boolean isFadingOut = false;
    private static boolean isStopping = false;

    private static float musicStartingSecond = 0;

    private static boolean nextMusicNeedsFadeIn = false;
    private static long nextMusicFadeInDuration = 0;
    private static EasingSystem.EasingType nextMusicFadeInEasingType;

    private static boolean isFadingIn = false;
    private static long fadeInStartTime = -1;
    private static long fadeInEndTime = -1;
    private static EasingSystem.EasingType currentFadeInEasingType;

    public static void playMusic(SoundEvent newMusic, long fadeDuration, EasingSystem.EasingType fadeType) {
        playMusic(newMusic, fadeDuration, 0, fadeType);
    }

    public static void playMusic(SoundEvent newMusic, long fadeDuration, float startingTime, EasingSystem.EasingType fadeType) {
        SoundInstance newSoundInstance = SimpleSoundInstance.forMusic(newMusic);

        CMSharedConstants.LOGGER.info("Playing music: {}", newMusic.getLocation());

        musicStartingSecond = startingTime;

        if (currentMusic == null && !isFadingOut) {
            currentMusic = newSoundInstance;
            minecraft.getSoundManager().play(currentMusic);

            if (startingTime != 0) {
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 0.0f);
                fadeInStartTime = System.currentTimeMillis();
                fadeInEndTime = fadeInStartTime + fadeDuration;
                currentFadeInEasingType = fadeType;
                isFadingIn = true;
            } else {
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 1.0f);
            }
        } else {
            if (currentMusic != null) {
                fadingOutMusics.add(currentMusic);
            }
            currentMusic = null;
            nextMusic = newSoundInstance;

            if (startingTime != 0) {
                nextMusicNeedsFadeIn = true;
                nextMusicFadeInDuration = fadeDuration;
                nextMusicFadeInEasingType = fadeType;
            } else {
                nextMusicNeedsFadeIn = false;
            }

            currentFadeType = fadeType;
            fadeStartTime = System.currentTimeMillis();
            fadeEndTime = fadeStartTime + fadeDuration;
            isFadingOut = true;
            isStopping = false;
        }
    }

    public static void stopMusic(long fadeDuration, EasingSystem.EasingType fadeType) {
        if (currentMusic == null && fadingOutMusics.isEmpty()) return;

        if (currentMusic != null) {
            CMSharedConstants.LOGGER.info("Stopping music: {}", currentMusic.getLocation());
        }
        
        if (fadeDuration > 0) {
            // Start fading out the current music
            if (currentMusic != null) {
                fadingOutMusics.add(currentMusic);
            }
            currentMusic = null;
            nextMusic = null;

            currentFadeType = fadeType;
            fadeStartTime = System.currentTimeMillis();
            fadeEndTime = fadeStartTime + fadeDuration;

            isFadingOut = true;
            isStopping = true;
        } else {
            // Stop immediately
            if (currentMusic != null) stop(currentMusic);
            for (SoundInstance music : fadingOutMusics) stop(music);
            fadingOutMusics.clear();
            currentMusic = null;
            nextMusic = null;
            isFadingOut = false;
            isStopping = false;
        }
    }

    private static void stop(SoundInstance soundInstance) {
        if (minecraft.getSoundManager().soundEngine.loaded) {
            ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle) minecraft.getSoundManager().soundEngine.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(Channel::stop);
            }
        }
    }

    public static void tick() {
        long currentTime = System.currentTimeMillis();

        // Fade-out, old musics
        Iterator<SoundInstance> iterator = fadingOutMusics.iterator();
        while (iterator.hasNext()) {
            SoundInstance oldMusic = iterator.next();
            double volume = EasingSystem.getEasedValue(fadeStartTime, fadeEndTime, 1, 0, currentFadeType);

            if (currentTime >= fadeEndTime) {
                stop(oldMusic);
                iterator.remove();
            } else {
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(oldMusic, (float) volume);
            }
        }

        // Fade-out
        if (isFadingOut && fadingOutMusics.isEmpty()) {
            isFadingOut = false;
            if (isStopping) {
                currentMusic = null;
                isStopping = false;
            } else if (nextMusic != null) {
                currentMusic = nextMusic;
                nextMusic = null;
                minecraft.getSoundManager().play(currentMusic);

                if (nextMusicNeedsFadeIn) {
                    ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 0.0f);
                    fadeInStartTime = System.currentTimeMillis();
                    fadeInEndTime = fadeInStartTime + nextMusicFadeInDuration;
                    currentFadeInEasingType = nextMusicFadeInEasingType;
                    isFadingIn = true;
                    nextMusicNeedsFadeIn = false;
                } else {
                    ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 1.0f);
                }
            }
        }

        // Fade-in
        if (isFadingIn) {
            if (currentTime >= fadeInEndTime) {
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 1.0f);
                isFadingIn = false;
            } else {
                double volume = EasingSystem.getEasedValue(fadeInStartTime, fadeInEndTime, 0, 1, currentFadeInEasingType);
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, (float) volume);
            }
        }
    }

    public static float getCurrentVolume() {
        return (float) currentVolume;
    }

    public static float getMusicStartingSecond() {
        return musicStartingSecond;
    }
}