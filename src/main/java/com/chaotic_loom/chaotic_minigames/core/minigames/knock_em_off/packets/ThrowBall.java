package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.packets;

import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.KnockEmOff;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class ThrowBall {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "throw_ball");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {

    }

    public static void sendToAll(MinecraftServer server, ServerPlayer juggernaut, Vector3f pos, Vector2f rot) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(juggernaut.getName().getString());
        friendlyByteBuf.writeVector3f(pos);
        friendlyByteBuf.writeVector3f(new Vector3f(rot.x, rot.y, 0));

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
