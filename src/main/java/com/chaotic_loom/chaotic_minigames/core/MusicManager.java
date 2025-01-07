package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.mixin_extra.SoundManagerExtra;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MusicManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static List<SoundInstance> oldMusics = new ArrayList<>();
    private static SoundInstance currentMusic;

    private static long fadeStartTime = -1;
    private static long fadeEndTime = -1;

    private static double currentVolume = 1.0;

    private static EasingSystem.EasingType currentFadeType = EasingSystem.EasingType.LINEAR;

    private static boolean isFadingOut = false;

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

        if (fadeDuration > 0) {
            // Fade out the current music
            oldMusics.add(currentMusic);
            currentFadeType = fadeType;
            fadeStartTime = System.currentTimeMillis();
            fadeEndTime = fadeStartTime + fadeDuration;
            isFadingOut = true;
        } else {
            // Stop immediately
            minecraft.getSoundManager().stop(currentMusic);
            currentMusic = null;
            isFadingOut = false;
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
                minecraft.getSoundManager().stop(oldMusic);
                iterator.remove();
            } else {
                // Apply volume during fade-out
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(oldMusic, (float) volume);
            }
        }

        // If the old music has finished fading out, play the new one
        if (isFadingOut && oldMusics.isEmpty()) {
            isFadingOut = false;
            if (currentMusic != null) {
                minecraft.getSoundManager().play(currentMusic);
                ((SoundManagerExtra) minecraft.getSoundManager()).chaoticMinigames$setVolume(currentMusic, 1.0f);
            }
        }
    }

    public static float getCurrentVolume() {
        return (float) currentVolume;
    }
}