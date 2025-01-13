package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class KnownServerDataOnClient {
    public static ServerStatus.Type serverType;
    public static PartyStatus.State partyState;

    public static GenericMinigame currentMinigame;

    public static String minigameIdToVote1, minigameIdToVote2, minigameIdToVote3;
    public static MutableComponent minigameNameToVote1, minigameNameToVote2, minigameNameToVote3;
    public static MutableComponent minigameSummaryToVote1, minigameSummaryToVote2, minigameSummaryToVote3;
    public static ResourceLocation minigameImageToVote1, minigameImageToVote2, minigameImageToVote3;

    public static GenericMinigame nextMinigame;

    public static Vector3f frozenPosition = null;
}
