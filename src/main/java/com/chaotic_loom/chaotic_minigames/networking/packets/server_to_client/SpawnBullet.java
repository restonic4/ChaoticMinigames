package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.core.client.ClientBulletManager;
import com.chaotic_loom.chaotic_minigames.core.data.minigames.bullet_chaos.ClientBullet;
import com.chaotic_loom.under_control.UnderControl;
import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.client.rendering.effects.SphereManager;
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
import org.joml.Vector3f;

import java.awt.*;

public class SpawnBullet {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        Vector3f spawnPoint = friendlyByteBuf.readVector3f();
        Vector3f endPoint = friendlyByteBuf.readVector3f();
        long spawnTime = friendlyByteBuf.readLong();
        long deSpawnTime = friendlyByteBuf.readLong();
        float radius = friendlyByteBuf.readFloat();

        minecraft.execute(() -> {
            Sphere sphere = SphereManager.create(Util.getUniqueID());

            sphere.setPosition(spawnPoint);
            sphere.setRadius(radius);
            sphere.setColor(new Color(0xFF0000));

            ClientBulletManager.addBullet(new ClientBullet(sphere, spawnPoint, endPoint, spawnTime, deSpawnTime));
        });
    }

    public static void sendToAll(MinecraftServer server, Vector3f spawnPoint, Vector3f endPoint, long spawnTime, long deSpawnTime, float radius) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeVector3f(spawnPoint);
        friendlyByteBuf.writeVector3f(endPoint);
        friendlyByteBuf.writeLong(spawnTime);
        friendlyByteBuf.writeLong(deSpawnTime);
        friendlyByteBuf.writeFloat(radius);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }

    public static ResourceLocation getId() {
        return new ResourceLocation(UnderControl.MOD_ID, "spawn_bullet");
    }
}
