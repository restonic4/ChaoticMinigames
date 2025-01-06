package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.under_control.util.EasingSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.SampledFloat;

import java.util.ArrayList;
import java.util.List;

public class MusicManager {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static List<SoundInstance> oldMusics = new ArrayList<>();
    private static SoundInstance currentMusic;

    private static long fadeStartTime = -1;
    private static long fadeEndTime = -1;

    private static double currentVolume = 1.0;

    private static EasingSystem.EasingType currentFadeType = EasingSystem.EasingType.LINEAR;

    private static boolean playing = false;

    public static void playMusic(SoundEvent newMusic, long fadeDuration, EasingSystem.EasingType fadeType) {
        System.out.println("Time to play " + newMusic.getLocation());

        SoundInstance newSoundInstance = SimpleSoundInstance.forMusic(newMusic);

        if (currentMusic == null) {
            currentMusic = newSoundInstance;

            fadeStartTime = -1;
            fadeEndTime = -1;

            return;
        }

        oldMusics.add(currentMusic);
        currentMusic = newSoundInstance;

        fadeStartTime = System.currentTimeMillis();
        fadeEndTime = fadeStartTime + fadeDuration;
    }

    public static void stopMusic(long fadeDuration, EasingSystem.EasingType fadeType) {

    }


    public static void tick() {
        if (fadeStartTime == -1 || fadeEndTime == -1) {
            if (currentMusic != null && !playing) {
                playing = true;
                minecraft.getSoundManager().play(currentMusic);
            }

            return;
        }

        currentVolume = EasingSystem.getEasedValue(fadeStartTime, fadeEndTime, 1, 0, currentFadeType);

        if (System.currentTimeMillis() > fadeEndTime) {
            playing = false;

            fadeStartTime = -1;
            fadeEndTime = -1;
        }
    }

    public static float getCurrentVolume() {
        return (float) currentVolume;
    }
}
