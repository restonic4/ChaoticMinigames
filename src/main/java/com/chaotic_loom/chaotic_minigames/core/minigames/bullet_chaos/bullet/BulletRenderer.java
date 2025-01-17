package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet;

import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import org.joml.Vector3f;

public class BulletRenderer extends Bullet implements Poolable {
    private Sphere sphere;

    public BulletRenderer() {
        super(new Vector3f(), new Vector3f(), 0, 1);
    }

    public BulletRenderer(Sphere sphere, Vector3f spawnPoint, Vector3f endPoint, long spawnedTime, long endTime) {
        super(spawnPoint, endPoint, spawnedTime, endTime);

        this.sphere = sphere;
    }

    public BulletRenderer initialize(Sphere sphere, Vector3f spawnPoint, Vector3f endPoint, long spawnedTime, long endTime) {
        super.spawnPoint = spawnPoint;
        super.endPoint = endPoint;
        super.spawnedTime = spawnedTime;
        super.endTime = endTime;

        this.sphere = sphere;

        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            EffectManager.delete(sphere.getId());
            return;
        }

        sphere.setPosition(getPosition());
    }
}
