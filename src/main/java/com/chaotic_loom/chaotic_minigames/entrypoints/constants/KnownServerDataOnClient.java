package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class KnownServerDataOnClient {
    public static ServerStatus.Type serverType;
    public static PartyStatus.State partyState;

    public static String minigameIdToVote1, minigameIdToVote2, minigameIdToVote3;
    public static MutableComponent minigameNameToVote1, minigameNameToVote2, minigameNameToVote3;
    public static MutableComponent minigameSummaryToVote1, minigameSummaryToVote2, minigameSummaryToVote3;
    public static ResourceLocation minigameImageToVote1, minigameImageToVote2, minigameImageToVote3;
}
