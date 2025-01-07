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

    public static void playMusic(SoundEvent newMusic, long fadeDuration, EasingSystem.EasingType fadeType) {
        SoundInstance newSoundInstance = SimpleSoundInstance.forMusic(newMusic);

        CMSharedConstants.LOGGER.info("Playing music: {}", newMusic.getLocation());

        if (currentMusic == null && !isFadingOut) {
            // No music playing, play immediately
            currentMusic = newSoundInstance;
            currentVolume = 1.0;
            minecraft.getSoundManager().play(currentMusic);
        } else {
            // Queue the new music and start fading out the current one
            if (currentMusic != null) {
                fadingOutMusics.add(currentMusic);
            }
            currentMusic = null;
            nextMusic = newSoundInstance;

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

        // Handle fade-out for old musics
        Iterator<SoundInstance> iterator = fadingOutMusics.iterator();
        while (iterator.hasNext()) {
            SoundInstance oldMusic = iterator.next();
            double volume = EasingSystem.getEasedValue(fadeStartTime, fadeEndTime, 1, 0, currentFadeType);

            if (currentTime >= fadeEndTime) {
                // Fade-out complete, stop and remove old music
                stop(oldMusic);
                iterator.remove();
            } else {
                // Apply volume during fade-out
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(oldMusic, (float) volume);
            }
        }

        // Handle transition after fade-out
        if (isFadingOut && fadingOutMusics.isEmpty()) {
            isFadingOut = false;
            if (isStopping) {
                currentMusic = null;
                isStopping = false;
            } else if (nextMusic != null) {
                currentMusic = nextMusic;
                nextMusic = null;
                minecraft.getSoundManager().play(currentMusic);
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 1.0f);
            }
        }
    }

    public static float getCurrentVolume() {
        return (float) currentVolume;
    }
}


/*public class MusicManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static List<SoundInstance> oldMusics = new ArrayList<>();
    private static SoundInstance currentMusic;

    private static long fadeStartTime = -1;
    private static long fadeEndTime = -1;

    private static double currentVolume = 1.0;

    private static EasingSystem.EasingType currentFadeType = EasingSystem.EasingType.LINEAR;

    private static boolean isFadingOut = false;
    private static boolean isStopping = false;


    public static void playMusic(SoundEvent newMusic, long fadeDuration, EasingSystem.EasingType fadeType) {
        System.out.println("Time to play " + newMusic.getLocation());

        SoundInstance newSoundInstance = SimpleSoundInstance.forMusic(newMusic);

        if (currentMusic == null) {
            // No current music, play immediately
            currentMusic = newSoundInstance;
            currentVolume = 1.0;
            minecraft.getSoundManager().play(currentMusic);
            return;
        }

        // Fade out the current music before starting the new one
        oldMusics.add(currentMusic);
        currentMusic = newSoundInstance;
        currentFadeType = fadeType;
        fadeStartTime = System.currentTimeMillis();
        fadeEndTime = fadeStartTime + fadeDuration;
        isFadingOut = true;
    }

    public static void stopMusic(long fadeDuration, EasingSystem.EasingType fadeType) {
        if (currentMusic == null) return; // Nothing to stop

        System.out.println("Stopping " + currentMusic.getLocation());

        if (fadeDuration > 0) {
            // Fade out the current music
            oldMusics.add(currentMusic);
            currentFadeType = fadeType;
            fadeStartTime = System.currentTimeMillis();
            fadeEndTime = fadeStartTime + fadeDuration;
            isFadingOut = true;
            isStopping = true;
        } else {
            // Stop immediately
            stop(currentMusic);
            currentMusic = null;
            isFadingOut = false;
            isStopping = false;
        }
    }

    private static void stop(SoundInstance soundInstance) {
        if (minecraft.getSoundManager().soundEngine.loaded) {
            ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle)minecraft.getSoundManager().soundEngine.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(Channel::stop);
            }
        }
    }

    public static void tick() {
        long currentTime = System.currentTimeMillis();

        // Handle fade-out for old musics
        Iterator<SoundInstance> iterator = oldMusics.iterator();
        while (iterator.hasNext()) {
            SoundInstance oldMusic = iterator.next();
            double volume = EasingSystem.getEasedValue(fadeStartTime, fadeEndTime, 1, 0, currentFadeType);

            if (currentTime >= fadeEndTime) {
                // Fade-out complete, stop and remove old music
                stop(oldMusic);
                iterator.remove();
            } else {
                // Apply volume during fade-out
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(oldMusic, (float) volume);
            }
        }

        // If the old music has finished fading out, play the new one
        if (isFadingOut && oldMusics.isEmpty()) {
            isFadingOut = false;
            if (!isStopping && currentMusic != null) {
                // Play only if the music is not stopping
                minecraft.getSoundManager().play(currentMusic);
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 1.0f);
            }
            if (isStopping) {
                currentMusic = null;
                isStopping = false;
            }
        }

    }

    public static float getCurrentVolume() {
        return (float) currentVolume;
    }
}*/