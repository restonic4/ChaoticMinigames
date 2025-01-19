package com.chaotic_loom.chaotic_minigames.core.minigames.mine_and_shine.packets;

import com.chaotic_loom.chaotic_minigames.core.minigames.mine_and_shine.MineAndShine;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class ShowEmeralds {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "show_emeralds");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        int posTotal = friendlyByteBuf.readInt();

        List<BlockPos> blockPos = new ArrayList<>();

        for (int i = 0; i < posTotal; i++) {
            BlockPos pos = friendlyByteBuf.readBlockPos();
            blockPos.add(pos);
        }

        MineAndShine.setEmeraldsList(blockPos);
    }

    public static void sendToAll(MinecraftServer server, List<BlockPos> blockPos) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeInt(blockPos.size());

        for (BlockPos pos : blockPos) {
            friendlyByteBuf.writeBlockPos(pos);
        }

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
