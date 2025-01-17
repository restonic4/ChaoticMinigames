package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.registries.client.UnderControlShaders;
import com.chaotic_loom.under_control.util.data_holders.RenderingFlags;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class DeleteSkySphere {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "delete_sky_sphere");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        String sphereID = friendlyByteBuf.readUtf();
        EffectManager.delete(sphereID);
    }

    public static void sendToAll(MinecraftServer server, String sphereID) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(sphereID);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
