package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.client.gui.VotingScreen;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class SendVoteDataToClient {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "send_vote_data_to_client");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        KnownServerDataOnClient.minigameIdToVote1 = friendlyByteBuf.readUtf();
        KnownServerDataOnClient.minigameIdToVote2 = friendlyByteBuf.readUtf();
        KnownServerDataOnClient.minigameIdToVote3 = friendlyByteBuf.readUtf();

        System.out.printf("Minigames to vote: " + KnownServerDataOnClient.minigameIdToVote1 + ", " + KnownServerDataOnClient.minigameIdToVote2 + " & " + KnownServerDataOnClient.minigameIdToVote3);

        for (GenericMinigame genericMinigame : MinigameRegistry.MINIGAMES) {
            MinigameSettings settings = genericMinigame.getSettings();

            if (settings.getId().equals(KnownServerDataOnClient.minigameIdToVote1)) {
                KnownServerDataOnClient.minigameNameToVote1 = settings.getName();
                KnownServerDataOnClient.minigameSummaryToVote1 = settings.getSummary();
                KnownServerDataOnClient.minigameImageToVote1 = new ResourceLocation(CMSharedConstants.ID, "textures/minigames/" + settings.getId() + "/banner.png");
            }

            if (settings.getId().equals(KnownServerDataOnClient.minigameIdToVote2)) {
                KnownServerDataOnClient.minigameNameToVote2 = settings.getName();
                KnownServerDataOnClient.minigameSummaryToVote2 = settings.getSummary();
                KnownServerDataOnClient.minigameImageToVote2 = new ResourceLocation(CMSharedConstants.ID, "textures/minigames/" + settings.getId() + "/banner.png");
            }

            if (settings.getId().equals(KnownServerDataOnClient.minigameIdToVote3)) {
                KnownServerDataOnClient.minigameNameToVote3 = settings.getName();
                KnownServerDataOnClient.minigameSummaryToVote3 = settings.getSummary();
                KnownServerDataOnClient.minigameImageToVote3 = new ResourceLocation(CMSharedConstants.ID, "textures/minigames/" + settings.getId() + "/banner.png");
            }
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, String minigameId1, String minigameId2, String minigameId3) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(minigameId1);
        friendlyByteBuf.writeUtf(minigameId2);
        friendlyByteBuf.writeUtf(minigameId3);

        ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
    }

    public static void sendToAll(MinecraftServer server, String minigameId1, String minigameId2, String minigameId3) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(minigameId1);
        friendlyByteBuf.writeUtf(minigameId2);
        friendlyByteBuf.writeUtf(minigameId3);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
