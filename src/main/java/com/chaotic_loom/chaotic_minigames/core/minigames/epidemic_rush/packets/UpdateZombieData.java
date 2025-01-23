package com.chaotic_loom.chaotic_minigames.core.minigames.epidemic_rush.packets;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.List;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class UpdateZombieData {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "update_zombie_data");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        int zombiesTotal = friendlyByteBuf.readInt();

        KnownServerDataOnClient.zombiePlayersUUIDs.clear();

        for (int i = 0; i < zombiesTotal; i++) {
            String uuid = friendlyByteBuf.readUtf();
            KnownServerDataOnClient.zombiePlayersUUIDs.add(uuid);
        }
    }

    public static void sendToAll(MinecraftServer server, List<ServerPlayer> zombies) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeInt(zombies.size());

        for (ServerPlayer zombie : zombies) {
            friendlyByteBuf.writeUtf(zombie.getGameProfile().getId().toString());
        }

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
