package com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.packets;

import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.QuickDrop;
import com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.crusher.CrusherRenderer;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.Sweeper;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.SpinningBarRenderer;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.client.rendering.effects.Cube;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
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

import java.awt.*;

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class SpawnCrusher {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "spawn_crusher");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        Vector3f position = friendlyByteBuf.readVector3f();
        float startingHeight = friendlyByteBuf.readFloat();
        long startTime = friendlyByteBuf.readLong();
        long endTime = friendlyByteBuf.readLong();
        String id = friendlyByteBuf.readUtf();

        minecraft.execute(() -> {
            Cube cube = (Cube) EffectManager.add(new Cube(id));

            cube.setPosition(position);
            cube.setColor(new Color(0xFF0000));
            cube.setScale(new Vector3f(100, 0.5f, 100));

            GenericMinigame.getInstance(QuickDrop.class).setClientCrusher(
                    PoolManager.acquire(CrusherRenderer.class).initialize(cube, position, startingHeight, startTime, endTime)
            );
        });
    }

    public static void sendToAll(MinecraftServer server, Vector3f position, float startingHeight, long startTime, long endTime, String id) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeVector3f(position);
        friendlyByteBuf.writeFloat(startingHeight);
        friendlyByteBuf.writeLong(startTime);
        friendlyByteBuf.writeLong(endTime);
        friendlyByteBuf.writeUtf(id);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
