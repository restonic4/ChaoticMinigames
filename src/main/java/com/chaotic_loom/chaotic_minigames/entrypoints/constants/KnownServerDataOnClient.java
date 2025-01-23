package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class KnownServerDataOnClient {
    public static ServerStatus.Type serverType;
    public static PartyStatus.State partyState;

    public static GenericMinigame currentMinigame;
    public static GenericMinigame nextMinigame;

    public static String minigameIdToVote1, minigameIdToVote2, minigameIdToVote3;
    public static MutableComponent minigameNameToVote1, minigameNameToVote2, minigameNameToVote3;
    public static MutableComponent minigameSummaryToVote1, minigameSummaryToVote2, minigameSummaryToVote3;
    public static ResourceLocation minigameImageToVote1, minigameImageToVote2, minigameImageToVote3;

    public static Vector3f frozenPosition = null;

    public static List<String> zombiePlayersUUIDs = new ArrayList<>();

    public static void clear() {
        serverType = null;
        partyState = null;

        currentMinigame = null;
        nextMinigame = null;

        frozenPosition = null;

        zombiePlayersUUIDs.clear();
    }
}
