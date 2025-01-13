package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.client.gui.VotingScreen;
import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.under_control.UnderControl;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.client.ClientCacheData;
import com.chaotic_loom.under_control.config.ConfigProvider;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class SendServerDataToClient {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "send_server_data_to_client");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        KnownServerDataOnClient.serverType = ServerStatus.Type.valueOf(friendlyByteBuf.readUtf());

        if (KnownServerDataOnClient.serverType == ServerStatus.Type.PARTY) {
            KnownServerDataOnClient.partyState = PartyStatus.State.valueOf(friendlyByteBuf.readUtf());

            KnownServerDataOnClient.currentMinigame = null;

            String minigameId = friendlyByteBuf.readUtf();

            if (!minigameId.equals("null")) {
                for (GenericMinigame genericMinigame : MinigameRegistry.MINIGAMES) {
                    if (genericMinigame.getSettings().getId().equals(minigameId)) {
                        KnownServerDataOnClient.currentMinigame = genericMinigame;
                    }
                }
            }

            if (KnownServerDataOnClient.partyState != PartyStatus.State.VOTING && minecraft.screen instanceof VotingScreen) {
                minecraft.execute(() -> {
                    minecraft.setScreen(null);
                });
            }
        }

        System.out.println(KnownServerDataOnClient.serverType + " -> " + KnownServerDataOnClient.partyState);
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        GameManager gameManager = GameManager.getInstance();
        PartyManager partyManager = gameManager.getPartyManager();

        if (gameManager.getServerStatus().getType() == null) {
            return;
        }

        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(gameManager.getServerStatus().getType().name());

        if (partyManager != null) {
            friendlyByteBuf.writeUtf(partyManager.getPartyStatus().getState().name());

            if (partyManager.getCurrentMinigame() != null) {
                friendlyByteBuf.writeUtf(partyManager.getCurrentMinigame().getSettings().getId());
            } else {
                friendlyByteBuf.writeUtf("null");
            }
        }

        ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
    }
}
