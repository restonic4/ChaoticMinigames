package com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public abstract class SpinningBar implements Poolable {
    public static final float HITBOX_HEIGHT = 0.25f;
    public static float hitbox_radius = 0.25f;

    protected Vector3f position;
    protected float radius;
    protected long startTime;
    protected long endTime;
    protected int spins;
    protected boolean finished = false;

    private float angleOffset = 10f;

    public SpinningBar(Vector3f position, float radius, long startTime, long endTime, int spins) {
        this.position = position;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
        this.spins = spins;
    }

    protected float calculatePosition(Vector3f result1, Vector3f result2) {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        float progress = EasingSystem.getEasedValue(currentTime, startTime, endTime, 0, 1, EasingSystem.EasingType.QUAD_IN);

        hitbox_radius = 0.25f + 2.5f * progress;

        float angleOffsetRadians = (float) Math.toRadians(angleOffset);
        float angle = progress * spins * (float) Math.PI * 2 + angleOffsetRadians;

        float x1 = position.x + radius * (float) Math.cos(angle);
        float z1 = position.z + radius * (float) Math.sin(angle);
        float x2 = position.x - radius * (float) Math.cos(angle);
        float z2 = position.z - radius * (float) Math.sin(angle);

        result1.set(x1, position.y, z1);
        result2.set(x2, position.y, z2);

        adjustCollisionPoint(result1);
        adjustCollisionPoint(result2);

        return (float) -Math.toDegrees(angle);
    }

    private void adjustCollisionPoint(Vector3f collisionPoint) {
        Player closestPlayer = getClosestPlayer(collisionPoint);

        if (closestPlayer != null) {
            float relativeX = collisionPoint.x - position.x;
            float relativeZ = collisionPoint.z - position.z;

            float distanceToCenter = (float) Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);

            float playerRelativeX = (float) (closestPlayer.position().x() - position.x);
            float playerRelativeZ = (float) (closestPlayer.position().z() - position.z);
            float playerDistanceToCenter = (float) Math.sqrt(playerRelativeX * playerRelativeX + playerRelativeZ * playerRelativeZ);

            float scale = playerDistanceToCenter / distanceToCenter;

            collisionPoint.x = position.x + relativeX * scale;
            collisionPoint.z = position.z + relativeZ * scale;
        }
    }

    abstract Player getClosestPlayer(Vector3f collisionPoint);

    private final Vector3f cachePosition1 = new Vector3f();
    private final Vector3f cachePosition2 = new Vector3f();
    private float currentAngle = 0;
    public void tick() {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        if (currentTime >= endTime) {
            finished = true;
            return;
        }

        currentAngle = calculatePosition(cachePosition1, cachePosition2);
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinish() {
        this.finished = true;
    }

    public Vector3f getPosition1() {
        return cachePosition1;
    }

    public Vector3f getPosition2() {
        return cachePosition2;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    @Override
    public void reset() {
        this.finished = false;
    }
}
