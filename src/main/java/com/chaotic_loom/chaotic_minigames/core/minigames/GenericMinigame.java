package com.chaotic_loom.chaotic_minigames.core.minigames;

import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import net.minecraft.server.level.ServerPlayer;

public abstract class GenericMinigame {
    public static int GENERIC_MAX_PLAYERS = 30;

    private final MinigameSettings minigameSettings;

    public GenericMinigame(MinigameSettings minigameSettings) {
        this.minigameSettings = minigameSettings;
    }

    public MinigameSettings getSettings() {
        return this.minigameSettings;
    }

    public boolean canBeStarted(int playerCount) {
        return playerCount >= minigameSettings.getMinPlayers() && playerCount <= minigameSettings.getMaxPlayers();
    }

    public abstract void onStart(PartyManager partyManager);

    public void awardPlayer(ServerPlayer serverPlayer) {
        System.out.println("Awarding " + serverPlayer.getName());
    }

    public static MapList<MapData> createMaps(MapData... maps) {
        MapList<MapData> list = new MapList<MapData>();

        for (MapData mapData : maps) {
            list.insert(mapData);
        }

        return list;
    }

    public static MapList<MapSpawn> createSpawns(MapSpawn... spawns) {
        MapList<MapSpawn> list = new MapList<MapSpawn>();

        for (MapSpawn spawn : spawns) {
            list.insert(spawn);
        }

        return list;
    }
}
