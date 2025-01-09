package com.chaotic_loom.chaotic_minigames;

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
}
