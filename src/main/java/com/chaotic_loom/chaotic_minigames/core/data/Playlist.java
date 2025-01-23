package com.chaotic_loom.chaotic_minigames.core.data;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.PlayMusic;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Playlist {
    private final List<MusicHolder> musics;
    private final Random random;

    public Playlist() {
        this.musics = new ArrayList<>();
        this.random = new Random();
    }

    public void addMusic(SoundEvent music) {
        this.musics.add(new MusicHolder(music, 0));
    }

    public void addMusic(SoundEvent music, float startTime) {
        this.musics.add(new MusicHolder(music, startTime));
    }

    public SoundEvent getRandom() {
        return getRandomHolder().getSoundEvent();
    }

    public MusicHolder getRandomHolder() {
        if (musics.isEmpty()) {
            return null;
        }

        int index = random.nextInt(musics.size());
        return musics.get(index);
    }

    public void playRandom() {
        playRandom(2000);
    }

    public void playRandom(long fadeDuration) {
        playRandom(fadeDuration, EasingSystem.EasingType.LINEAR);
    }

    public void playRandom(long fadeDuration, EasingSystem.EasingType easingType) {
        MusicHolder music = getRandomHolder();

        if (music == null) {
            throw new RuntimeException("Empty playlist");
        }

        PlayMusic.sendToAll(GameManager.getInstance().getServer(), music.getSoundEvent(), fadeDuration, music.getStartTime(), easingType);
    }

    public List<MusicHolder> getMusics() {
        return musics;
    }

    public static class MusicHolder {
        private final SoundEvent soundEvent;
        private final float startTime;

        public MusicHolder(SoundEvent soundEvent, float startTime) {
            this.soundEvent = soundEvent;
            this.startTime = startTime;
        }

        public SoundEvent getSoundEvent() {
            return soundEvent;
        }

        public float getStartTime() {
            return startTime;
        }
    }
}
