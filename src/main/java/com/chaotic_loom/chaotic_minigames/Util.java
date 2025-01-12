package com.chaotic_loom.chaotic_minigames;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.Random;

public class Util {
    public static float calculateDistance(Vector3f jomlVector, net.minecraft.world.phys.Vec3 mojangVector) {
        float dx = (float) (jomlVector.x - mojangVector.x);
        float dy = (float) (jomlVector.y - mojangVector.y);
        float dz = (float) (jomlVector.z - mojangVector.z);

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
