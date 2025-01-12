package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import org.joml.Vector3f;

public abstract class Bullet {
    protected final Vector3f spawnPoint, endPoint;
    protected final long spawnedTime, endTime;
    protected boolean finished = false;

    public Bullet(Vector3f spawnPoint, Vector3f endPoint, long spawnedTime, long endTime) {
        this.spawnPoint = spawnPoint;
        this.endPoint = endPoint;
        this.spawnedTime = spawnedTime;
        this.endTime = endTime;
    }

    public boolean isFinished() {
        return finished;
    }

    protected void calculatePosition(Vector3f result) {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        float xProgress = EasingSystem.getEasedValue(currentTime, spawnedTime, endTime, spawnPoint.x, endPoint.x, EasingSystem.EasingType.LINEAR);
        float yProgress = EasingSystem.getEasedValue(currentTime, spawnedTime, endTime, spawnPoint.y, endPoint.y, EasingSystem.EasingType.LINEAR);
        float zProgress = EasingSystem.getEasedValue(currentTime, spawnedTime, endTime, spawnPoint.z, endPoint.z, EasingSystem.EasingType.LINEAR);

        result.set(xProgress, yProgress, zProgress);
    }

    private final Vector3f cachePosition = new Vector3f();
    public void tick() {
        long currentTime = System.currentTimeMillis();

        if (currentTime >= endTime) {
            finished = true;
            return;
        }

        calculatePosition(cachePosition);
    }

    public Vector3f getPosition() {
        return cachePosition;
    }
}
