package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.packets;

import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.KnockEmOff;
import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball.Ball;
import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball.BallRenderer;
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
        float yLevel = friendlyByteBuf.readFloat();
        float radius = friendlyByteBuf.readFloat();
        long startTime = friendlyByteBuf.readLong();
        long endTime = friendlyByteBuf.readLong();

        Vector3f originPoint = friendlyByteBuf.readVector3f();
        Vector3f leftPoint = friendlyByteBuf.readVector3f();
        Vector3f collisionPoint = friendlyByteBuf.readVector3f();
        Vector3f backLeftPoint = friendlyByteBuf.readVector3f();
        Vector3f backPoint = friendlyByteBuf.readVector3f();
        Vector3f backRightPoint = friendlyByteBuf.readVector3f();
        Vector3f inverseCollisionPoint = friendlyByteBuf.readVector3f();
        Vector3f rightPoint = friendlyByteBuf.readVector3f();

        BallRenderer ball = new BallRenderer(yLevel, radius, startTime, endTime);

        ball.setOriginPoint(new Vector2f(originPoint.x, originPoint.z));
        ball.setLeftCornerControlPoint(new Vector2f(leftPoint.x, leftPoint.z));
        ball.setCollisionPoint(new Vector2f(collisionPoint.x, collisionPoint.z));
        ball.setBackLeftCornerControlPoint(new Vector2f(backLeftPoint.x, backLeftPoint.z));
        ball.setBackPoint(new Vector2f(backPoint.x, backPoint.z));
        ball.setBackRightCornerControlPoint(new Vector2f(backRightPoint.x, backRightPoint.z));
        ball.setInverseCollisionPoint(new Vector2f(inverseCollisionPoint.x, inverseCollisionPoint.z));
        ball.setRightCornerControlPoint(new Vector2f(rightPoint.x, rightPoint.z));

        KnockEmOff.setBall(ball);
    }

    public static void sendToAll(MinecraftServer server, float yLevel, float radius, long startTime, long endTime, Vector3f origin, Vector3f left, Vector3f collision, Vector3f backLeft, Vector3f back, Vector3f backRight, Vector3f inverse, Vector3f right) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeFloat(yLevel);
        friendlyByteBuf.writeFloat(radius);
        friendlyByteBuf.writeLong(startTime);
        friendlyByteBuf.writeLong(endTime);

        friendlyByteBuf.writeVector3f(origin);
        friendlyByteBuf.writeVector3f(left);
        friendlyByteBuf.writeVector3f(collision);
        friendlyByteBuf.writeVector3f(backLeft);
        friendlyByteBuf.writeVector3f(back);
        friendlyByteBuf.writeVector3f(backRight);
        friendlyByteBuf.writeVector3f(inverse);
        friendlyByteBuf.writeVector3f(right);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
