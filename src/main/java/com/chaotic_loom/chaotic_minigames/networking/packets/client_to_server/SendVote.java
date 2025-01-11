package com.chaotic_loom.chaotic_minigames.networking.packets.client_to_server;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.UnderControl;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.networking.packets.server_to_client.SendCurrentServerTime;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@Packet(direction = PacketDirection.CLIENT_TO_SERVER)
public class SendVote {
    public static ResourceLocation getId() {
        return new ResourceLocation(UnderControl.MOD_ID, "send_vote");
    }

    public static void receive(MinecraftServer server, Player player, ServerPacketListener serverPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        String minigameId = friendlyByteBuf.readUtf();

        if (player instanceof ServerPlayer serverPlayer) {
            GameManager.getInstance().getPartyManager().voteReceived(serverPlayer, minigameId);
        }
    }

    public static void sendToServer(String minigameId) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(minigameId);

        ClientPlayNetworking.send(getId(), friendlyByteBuf);
    }
}
