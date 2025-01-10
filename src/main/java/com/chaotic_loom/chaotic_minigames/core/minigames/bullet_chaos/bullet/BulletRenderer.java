package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet;

import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.client.rendering.effects.SphereManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import org.joml.Vector3f;

public class BulletRenderer extends Bullet {
    private final Sphere sphere;

    public BulletRenderer(Sphere sphere, Vector3f spawnPoint, Vector3f endPoint, long spawnedTime, long endTime) {
        super(spawnPoint, endPoint, spawnedTime, endTime);

        this.sphere = sphere;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            SphereManager.delete(sphere.getId());
            return;
        }

        sphere.setPosition(getPosition());
    }
}
