package com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.client.rendering.shader.ShaderProfile;
import com.chaotic_loom.under_control.core.annotations.Packet;
import com.chaotic_loom.under_control.core.annotations.PacketDirection;
import com.chaotic_loom.under_control.registries.client.UnderControlShaders;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.data_holders.RenderingFlags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Packet(direction = PacketDirection.SERVER_TO_CLIENT)
public class CreateSkySphere {
    public static ResourceLocation getId() {
        return new ResourceLocation(CMSharedConstants.ID, "create_sky_sphere");
    }

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        String sphereID = friendlyByteBuf.readUtf();

        int topColorLength = friendlyByteBuf.readInt();
        float[] topColor = new float[topColorLength];
        for (int i = 0; i < topColorLength; i++) {
            topColor[i] = friendlyByteBuf.readFloat();
        }

        int bottomColorLength = friendlyByteBuf.readInt();
        float[] bottomColor = new float[bottomColorLength];
        for (int i = 0; i < bottomColorLength; i++) {
            bottomColor[i] = friendlyByteBuf.readFloat();
        }

        Vector3f position = friendlyByteBuf.readVector3f();
        float radius = friendlyByteBuf.readFloat();

        ShaderProfile shaderProfile = new ShaderProfile(UnderControlShaders.VERTICAL_GRADIENT);
        shaderProfile.setUniformData("TopColor", topColor);
        shaderProfile.setUniformData("BottomColor", bottomColor);
        shaderProfile.setUniformData("Center", new float[]{position.x(), position.y(), position.z()});
        shaderProfile.setUniformData("Radius", new float[]{radius});

        minecraft.execute(() -> {
            Sphere sphere = (Sphere) EffectManager.add(new Sphere(sphereID));
            sphere.setPosition(position);
            sphere.setScale(new Vector3f(radius));
            sphere.setShaderProfile(shaderProfile);
            sphere.setRenderingFlags(RenderingFlags.INVERT_NORMALS);
        });
    }

    public static void sendToAll(MinecraftServer server, String sphereID, float[] topColor, float[] bottomColor, Vector3f position, float radius) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeUtf(sphereID);

        friendlyByteBuf.writeInt(topColor.length);
        for (float value : topColor) {
            friendlyByteBuf.writeFloat(value);
        }

        friendlyByteBuf.writeInt(bottomColor.length);
        for (float value : bottomColor) {
            friendlyByteBuf.writeFloat(value);
        }

        friendlyByteBuf.writeVector3f(position);
        friendlyByteBuf.writeFloat(radius);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, getId(), friendlyByteBuf);
        }
    }
}
