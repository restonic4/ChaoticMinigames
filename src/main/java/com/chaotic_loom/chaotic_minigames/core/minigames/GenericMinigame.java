package com.chaotic_loom.chaotic_minigames.core.minigames;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public abstract class GenericMinigame {
    public static int GENERIC_MAX_PLAYERS = 20;

    private final MinigameSettings minigameSettings;
    private boolean canTickOnServer = false;

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

    public abstract void tick(ExecutionSide executionSide);

    public void serverCleanup() {
        tick(ExecutionSide.SERVER);
    }

    public void clientCleanup() {
        tick(ExecutionSide.CLIENT);
    }

    public void startTickingOnServer() {
        this.canTickOnServer = true;
    }

    public void stopTickingOnServer() {
        this.canTickOnServer = false;
    }

    public boolean canTickOnServer() {
        return this.canTickOnServer;
    }

    public void awardPlayer(ServerPlayer serverPlayer) {
        System.out.println("Awarding " + serverPlayer.getName());
    }

    public void announceWinners() {
        MutableComponent component = Component.translatable("message.chaotic_minigames.winners");

        List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();

        if (players.isEmpty()) {
            component.append("Nobody");
        }

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer serverPlayer = players.get(i);
            Component playerName = serverPlayer.getDisplayName();

            if (players.size() == 1) {
                component.append(playerName);
            } else {
                if (i == players.size() - 1) {
                    component.append(" & ").append(playerName);
                } else if (i == 0) {
                    component.append(playerName);
                } else {
                    component.append(", ").append(playerName);
                }
            }
        }

        GameManager.getInstance().sendSubtitleToPlayers(component);
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

    public static <T extends GenericMinigame> T getInstance(Class<T> clazz) {
        for (GenericMinigame genericMinigame : MinigameRegistry.MINIGAMES) {
            if (clazz.isInstance(genericMinigame)) {
                return (T) genericMinigame;
            }
        }

        throw new ClassCastException("Minigame class not found " + clazz.getName());
    }
}
