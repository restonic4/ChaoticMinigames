package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.BezierCurve;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Ball implements Poolable {
    protected float yLevel;
    protected float radius;
    protected long startTime;
    protected long endTime;

    protected Vector3f position = new Vector3f();

    protected Vector2f originPoint, collisionPoint, inverseCollisionPoint, backPoint, leftCornerControlPoint, backLeftCornerControlPoint, backRightCornerControlPoint, rightCornerControlPoint;

    protected boolean finished = false;

    public Ball(float yLevel, float radius, long startTime, long endTime) {
        this.yLevel = yLevel;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private final BezierCurve cacheBezierCurve = new BezierCurve();
    protected void calculatePosition(Vector3f result) {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        if (!hasBeenInitialized()) {
            return;
        }

        float globalProgress = MathHelper.getProgress(currentTime, startTime, endTime);
        int phase = calculatePhase(globalProgress);
        float phaseProgress = calculatePhaseProgress(globalProgress, phase);

        if (phase == 0) {
            cacheBezierCurve.modifyControlPoint(0, originPoint.x, originPoint.y);
            cacheBezierCurve.modifyControlPoint(1, leftCornerControlPoint.x, leftCornerControlPoint.y);
            cacheBezierCurve.modifyControlPoint(2, collisionPoint.x, collisionPoint.y);
        } else if (phase == 1) {
            cacheBezierCurve.modifyControlPoint(0, collisionPoint.x, collisionPoint.y);
            cacheBezierCurve.modifyControlPoint(1, backLeftCornerControlPoint.x, backLeftCornerControlPoint.y);
            cacheBezierCurve.modifyControlPoint(2, backPoint.x, backPoint.y);
        } else if (phase == 2) {
            cacheBezierCurve.modifyControlPoint(0, backPoint.x, backPoint.y);
            cacheBezierCurve.modifyControlPoint(1, backRightCornerControlPoint.x, backRightCornerControlPoint.y);
            cacheBezierCurve.modifyControlPoint(2, inverseCollisionPoint.x, inverseCollisionPoint.y);
        } else if (phase == 3) {
            cacheBezierCurve.modifyControlPoint(0, inverseCollisionPoint.x, inverseCollisionPoint.y);
            cacheBezierCurve.modifyControlPoint(1, leftCornerControlPoint.x, leftCornerControlPoint.y);
            cacheBezierCurve.modifyControlPoint(2, originPoint.x, originPoint.y);
        }

        float[] progress = EasingSystem.getEasedBezierValue(phaseProgress, cacheBezierCurve, EasingSystem.EasingType.QUAD_IN);

        result.set(progress[0], yLevel, progress[1]);
    }

    public void tick() {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        if (currentTime >= endTime) {
            finished = true;
            return;
        }

        calculatePosition(position);
    }

    private static int calculatePhase(float globalProgress) {
        return Math.min(3, (int) (globalProgress * 4));
    }

    private static float calculatePhaseProgress(float globalProgress, int phase) {
        float phaseStart = phase / 4.0f;
        float phaseEnd = (phase + 1) / 4.0f;
        return (globalProgress - phaseStart) / (phaseEnd - phaseStart);
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinish() {
        this.finished = true;
    }

    public boolean hasBeenInitialized() {
        return originPoint != null &&  collisionPoint != null && inverseCollisionPoint != null && backPoint != null && leftCornerControlPoint != null && backLeftCornerControlPoint != null && backRightCornerControlPoint != null && rightCornerControlPoint != null;
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void reset() {
        this.finished = false;
    }

    public Vector2f getOriginPoint() {
        return originPoint;
    }

    public void setOriginPoint(Vector2f originPoint) {
        this.originPoint = originPoint;
    }

    public Vector2f getCollisionPoint() {
        return collisionPoint;
    }

    public void setCollisionPoint(Vector2f collisionPoint) {
        this.collisionPoint = collisionPoint;
    }

    public Vector2f getInverseCollisionPoint() {
        return inverseCollisionPoint;
    }

    public void setInverseCollisionPoint(Vector2f inverseCollisionPoint) {
        this.inverseCollisionPoint = inverseCollisionPoint;
    }

    public Vector2f getBackPoint() {
        return backPoint;
    }

    public void setBackPoint(Vector2f backPoint) {
        this.backPoint = backPoint;
    }

    public Vector2f getBackLeftCornerControlPoint() {
        return backLeftCornerControlPoint;
    }

    public void setBackLeftCornerControlPoint(Vector2f backLeftCornerControlPoint) {
        this.backLeftCornerControlPoint = backLeftCornerControlPoint;
    }

    public Vector2f getLeftCornerControlPoint() {
        return leftCornerControlPoint;
    }

    public void setLeftCornerControlPoint(Vector2f leftCornerControlPoint) {
        this.leftCornerControlPoint = leftCornerControlPoint;
    }

    public Vector2f getBackRightCornerControlPoint() {
        return backRightCornerControlPoint;
    }

    public void setBackRightCornerControlPoint(Vector2f backRightCornerControlPoint) {
        this.backRightCornerControlPoint = backRightCornerControlPoint;
    }

    public Vector2f getRightCornerControlPoint() {
        return rightCornerControlPoint;
    }

    public void setRightCornerControlPoint(Vector2f rightCornerControlPoint) {
        this.rightCornerControlPoint = rightCornerControlPoint;
    }
}
