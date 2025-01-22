package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import org.joml.Vector3f;

public class BallRenderer extends Ball implements Poolable {
    private Sphere sphere;

    public BallRenderer(float radius, long startTime, long endTime) {
        super(radius, startTime, endTime);
        sphere = (Sphere) EffectManager.add(new Sphere("ball" + MathHelper.getUniqueID()));
    }

    public BallRenderer initialize(float radius, long startTime, long endTime) {
        super.radius = radius;
        super.startTime = startTime;
        super.endTime = endTime;

        sphere = (Sphere) EffectManager.add(new Sphere("ball" + MathHelper.getUniqueID()));

        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            EffectManager.delete(sphere.getId());
            return;
        }

        sphere.setPosition(position);
    }

    @Override
    public void markFinish() {
        super.markFinish();
        PoolManager.release(this);
    }
}
