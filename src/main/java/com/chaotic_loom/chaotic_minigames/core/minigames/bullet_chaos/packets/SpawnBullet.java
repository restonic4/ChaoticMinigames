package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.packets;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.BulletChaos;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet.BulletRenderer;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.util.MathHelper;
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
public class SpawnBullet {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "spawn_bullet");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        Vector3f spawnPoint = friendlyByteBuf.readVector3f();
        Vector3f endPoint = friendlyByteBuf.readVector3f();
        long spawnTime = friendlyByteBuf.readLong();
        long deSpawnTime = friendlyByteBuf.readLong();
        float radius = friendlyByteBuf.readFloat();
        String id = friendlyByteBuf.readUtf();

        minecraft.execute(() -> {
            Sphere sphere = (Sphere) EffectManager.add(new Sphere(id));

            sphere.setPosition(spawnPoint);
            sphere.setScale(new Vector3f(radius));
            sphere.setColor(new Color(0xFF0000));

            GenericMinigame.getInstance(BulletChaos.class).getClientBulletManager().addBullet(
                    PoolManager.acquire(BulletRenderer.class).initialize(sphere, spawnPoint, endPoint, spawnTime, deSpawnTime)
            );
        });
    }

    public static void sendToAll(MinecraftServer server, Vector3f spawnPoint, Vector3f endPoint, long spawnTime, long deSpawnTime, float radius, String id) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeVector3f(spawnPoint);
        friendlyByteBuf.writeVector3f(endPoint);
        friendlyByteBuf.writeLong(spawnTime);
        friendlyByteBuf.writeLong(deSpawnTime);
        friendlyByteBuf.writeFloat(radius);
        friendlyByteBuf.writeUtf(id);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
