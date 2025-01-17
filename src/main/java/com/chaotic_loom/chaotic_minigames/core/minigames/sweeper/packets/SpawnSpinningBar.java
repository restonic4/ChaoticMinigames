package com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.packets;

import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.BulletChaos;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet.BulletRenderer;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.Sweeper;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.SpinningBarRenderer;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.client.rendering.effects.Cube;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public class SpawnSpinningBar {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "spawn_spinning_bar");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        Vector3f position = friendlyByteBuf.readVector3f();
        float radius = friendlyByteBuf.readFloat();
        long startTime = friendlyByteBuf.readLong();
        long endTime = friendlyByteBuf.readLong();
        int spins = friendlyByteBuf.readInt();
        String id = friendlyByteBuf.readUtf();

        minecraft.execute(() -> {
            Cube cube = (Cube) EffectManager.add(new Cube(id));

            cube.setPosition(position);
            cube.setColor(new Color(0xFF0000));

            GenericMinigame.getInstance(Sweeper.class).setClientSpinningBar(
                    PoolManager.acquire(SpinningBarRenderer.class).initialize(cube, position, radius, startTime, endTime, spins)
            );
        });
    }

    public static void sendToAll(MinecraftServer server, Vector3f position, float radius, long startTime, long endTime, int spins, String id) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeVector3f(position);
        friendlyByteBuf.writeFloat(radius);
        friendlyByteBuf.writeLong(startTime);
        friendlyByteBuf.writeLong(endTime);
        friendlyByteBuf.writeInt(spins);
        friendlyByteBuf.writeUtf(id);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
