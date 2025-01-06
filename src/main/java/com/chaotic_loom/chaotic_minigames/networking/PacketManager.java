package com.chaotic_loom.chaotic_minigames.networking;

import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.SendServerDataToClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PacketManager {
    public static void registerServerToClient() {
        ClientPlayNetworking.registerGlobalReceiver(SendServerDataToClient.getId(), SendServerDataToClient::receive);
    }

    public static void registerClientToServer() {
        //ServerPlayNetworking.registerGlobalReceiver(ServerJoinRequest.getId(), ServerJoinRequest::receive);
    }
}
