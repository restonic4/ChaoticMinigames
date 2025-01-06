package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.under_control.UnderControl;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.client.ClientCacheData;
import com.chaotic_loom.under_control.config.ConfigProvider;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SendServerDataToClient {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        KnownServerDataOnClient.serverType = ServerStatus.Type.valueOf(friendlyByteBuf.readUtf());
        KnownServerDataOnClient.partyState = PartyStatus.State.valueOf(friendlyByteBuf.readUtf());
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        GameManager gameManager = GameManager.getInstance();
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(gameManager.getServerStatus().getType().name());
        friendlyByteBuf.writeUtf(gameManager.getPartyStatus().getState().name());

        ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
    }

    public static ResourceLocation getId() {
        return new ResourceLocation(UnderControl.MOD_ID, "send_server_data_to_client");
    }
}
