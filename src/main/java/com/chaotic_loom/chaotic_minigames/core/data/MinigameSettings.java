package com.chaotic_loom.chaotic_minigames.core.data;

import net.minecraft.network.chat.Component;

import java.util.List;

public class MinigameSettings {
    private String id;
    private int minPlayers, maxPlayers;
    private MapList<MapData> maps;

    public MinigameSettings(String id, int minPlayers, int maxPlayers, MapList<MapData> maps) {
        this.id = id;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.maps = maps;
    }

    public Component getName() {
        return Component.translatable("minigame.chaotic_minigames." + this.id + ".name");
    }

    public Component getDescription() {
        return Component.translatable("minigame.chaotic_minigames." + this.id + ".description");
    }

    public Component getSummary() {
        return Component.translatable("minigame.chaotic_minigames." + this.id + ".summary");
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public MapList<MapData> getMaps() {
        return maps;
    }

    public void setMaps(MapList<MapData> maps) {
        this.maps = maps;
    }
}