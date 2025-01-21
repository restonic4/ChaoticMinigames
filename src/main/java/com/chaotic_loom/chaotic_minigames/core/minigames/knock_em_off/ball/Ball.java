package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.BezierCurve;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Ball implements Poolable {
    protected Vector3f position;
    protected float radius;
    protected long startTime;
    protected long endTime;

    protected Vector2f startBezierPoint, midBezierPoint, endBezierPoint;

    protected boolean finished = false;

    public Ball(Vector3f position, float radius, long startTime, long endTime) {
        this.position = position;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private final BezierCurve cacheBezierCurve = new BezierCurve();
    protected void calculatePosition(Vector3f result) {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        if (startBezierPoint == null) {
            return;
        }

        cacheBezierCurve.modifyControlPoint(0, startBezierPoint.x, startBezierPoint.y);
        cacheBezierCurve.modifyControlPoint(1, midBezierPoint.x, midBezierPoint.y);
        cacheBezierCurve.modifyControlPoint(2, endBezierPoint.x, endBezierPoint.y);

        float[] progress = EasingSystem.getEasedBezierValue(currentTime, startTime, endTime, cacheBezierCurve, EasingSystem.EasingType.QUAD_IN);

        result.set(progress[0], result.y, progress[1]);
    }

    public void tick() {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        if (currentTime >= endTime) {
            finished = true;
            return;
        }

        calculatePosition(position);
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinish() {
        this.finished = true;
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void reset() {
        this.finished = false;
    }

    public Vector2f getStartBezierPoint() {
        return startBezierPoint;
    }

    public void setStartBezierPoint(Vector2f startBezierPoint) {
        this.startBezierPoint = startBezierPoint;
    }

    public Vector2f getMidBezierPoint() {
        return midBezierPoint;
    }

    public void setMidBezierPoint(Vector2f midBezierPoint) {
        this.midBezierPoint = midBezierPoint;
    }

    public Vector2f getEndBezierPoint() {
        return endBezierPoint;
    }

    public void setEndBezierPoint(Vector2f endBezierPoint) {
        this.endBezierPoint = endBezierPoint;
    }
}
