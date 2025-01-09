package com.chaotic_loom.chaotic_minigames.core.data.minigames.bullet_chaos;

import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.client.rendering.effects.SphereManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import org.joml.Vector3f;

public class ClientBullet {
    private final Sphere sphere;
    private final long spawnedTime, endTime;
    private final Vector3f spawnPoint, endPoint;

    boolean finished = false;

    public ClientBullet(Sphere sphere, Vector3f spawnPoint, Vector3f endPoint, long spawnedTime, long endTime) {
        this.endPoint = endPoint;
        this.spawnPoint = spawnPoint;
        this.endTime = endTime;
        this.spawnedTime = spawnedTime;
        this.sphere = sphere;
    }

    private final Vector3f cacheVec = new Vector3f();
    public void tick() {
        long currentTime = System.currentTimeMillis();

        if (currentTime >= endTime) {
            SphereManager.delete(sphere.getId());
            finished = true;
            return;
        }

        float xProgress = EasingSystem.getEasedValue(spawnedTime, endTime, spawnPoint.x, endPoint.x, EasingSystem.EasingType.LINEAR);
        float yProgress = EasingSystem.getEasedValue(spawnedTime, endTime, spawnPoint.y, endPoint.y, EasingSystem.EasingType.LINEAR);
        float zProgress = EasingSystem.getEasedValue(spawnedTime, endTime, spawnPoint.z, endPoint.z, EasingSystem.EasingType.LINEAR);

        cacheVec.set(xProgress, yProgress, zProgress);

        sphere.setPosition(cacheVec);
    }

    public boolean isFinished() {
        return finished;
    }
}
