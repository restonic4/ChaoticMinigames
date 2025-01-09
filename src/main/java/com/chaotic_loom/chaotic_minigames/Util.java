package com.chaotic_loom.chaotic_minigames;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.Random;

public class Util {
    public static Vector3f getRandomPointOnCircle(Vector3f initialPosition, float radius) {
        Random random = new Random();

        float angle = random.nextFloat() * (float) (2 * Math.PI);

        float x = initialPosition.x + radius * (float) Math.cos(angle);
        float z = initialPosition.z + radius * (float) Math.sin(angle);

        float y = initialPosition.y;

        return new Vector3f(x, y, z);
    }

    private static long lastID = -1;
    public static long getUniqueID() {
        lastID++;

        return lastID;
    }

    public static boolean isSphereCollidingWithAABB(Vector3f sphereCenter, float sphereRadius, AABB rect) {
        float closestX = Math.max((float) rect.minX, Math.min(sphereCenter.x, (float) rect.maxX));
        float closestY = Math.max((float) rect.minY, Math.min(sphereCenter.y, (float) rect.maxY));
        float closestZ = Math.max((float) rect.minZ, Math.min(sphereCenter.z, (float) rect.maxZ));

        Vector3f closestPoint = new Vector3f(closestX, closestY, closestZ);
        float distanceSquared = sphereCenter.distanceSquared(closestPoint);

        return distanceSquared <= sphereRadius * sphereRadius;
    }

    public static AABB getReducedPlayerAABB(ServerPlayer player, double scale) {
        AABB originalAABB = player.getBoundingBox();

        double centerX = (originalAABB.minX + originalAABB.maxX) / 2;
        double centerY = (originalAABB.minY + originalAABB.maxY) / 2;
        double centerZ = (originalAABB.minZ + originalAABB.maxZ) / 2;

        double halfWidthX = (originalAABB.maxX - originalAABB.minX) * scale / 2;
        double halfHeightY = (originalAABB.maxY - originalAABB.minY) * scale / 2;
        double halfWidthZ = (originalAABB.maxZ - originalAABB.minZ) * scale / 2;

        return new AABB(
                centerX - halfWidthX, centerY - halfHeightY, centerZ - halfWidthZ,
                centerX + halfWidthX, centerY + halfHeightY, centerZ + halfWidthZ
        );
    }
}
