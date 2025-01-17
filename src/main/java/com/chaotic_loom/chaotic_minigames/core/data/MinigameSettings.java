package com.chaotic_loom.chaotic_minigames.core.data;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class MinigameSettings {
    private String id;
    private ResourceLocation bannerImg;
    private int minPlayers, maxPlayers;
    private MapList<MapData> maps;

    public MinigameSettings(String id, int minPlayers, int maxPlayers, MapList<MapData> maps) {
        this.id = id;
        this.bannerImg = new ResourceLocation(CMSharedConstants.ID, "textures/minigames/" + id + "/banner.png");
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.maps = maps;
    }

    public String getId() {
        return id;
    }

    public MutableComponent getName() {
        return Component.translatable("minigame.chaotic_minigames." + this.id + ".name");
    }

    public MutableComponent getDescription() {
        return Component.translatable("minigame.chaotic_minigames." + this.id + ".description");
    }

    public MutableComponent getSummary() {
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

    public ResourceLocation getBannerImg() {
        return bannerImg;
    }
}
