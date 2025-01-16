package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.entrypoints.Client;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class SyncClients {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "sync_clients");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        GameManager.getInstance().getSynchronizationHelper().askForSynchronization();
        GameManager.getInstance().getSynchronizationHelper().dump();
        new Thread(() -> {
            try {
                Thread.sleep(4000);
            } catch (Exception ignored) {}

            GameManager.getInstance().getSynchronizationHelper().dump();
        }).start();
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
        ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
    }

    public static void sendToAll(MinecraftServer server) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
