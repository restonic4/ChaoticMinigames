package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.UnderControl;
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
public class StopMusic {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "stop_music");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        long fadeDuration = friendlyByteBuf.readLong();
        EasingSystem.EasingType easingType = EasingSystem.EasingType.valueOf(friendlyByteBuf.readUtf());

        MusicManager.stopMusic(fadeDuration, easingType);
    }

    public static void sendToClient(ServerPlayer serverPlayer, long fadeDuration, EasingSystem.EasingType easingType) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeLong(fadeDuration);
        friendlyByteBuf.writeUtf(easingType.name().toUpperCase());

        ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
    }

    public static void sendToAll(MinecraftServer server, long fadeDuration, EasingSystem.EasingType easingType) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeLong(fadeDuration);
        friendlyByteBuf.writeUtf(easingType.name().toUpperCase());

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
