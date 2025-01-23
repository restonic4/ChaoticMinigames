package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.client.rendering.effects.Sphere;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import net.minecraft.client.Minecraft;
import org.joml.Vector3f;

public class BallRenderer extends Ball implements Poolable {
    private Sphere sphere;

    public BallRenderer(float yLevel, float radius, long startTime, long endTime) {
        super(yLevel, radius, startTime, endTime);
        sphere = new Sphere("ball" + MathHelper.getUniqueID());

        Minecraft.getInstance().execute(() -> {
            EffectManager.add(sphere);
        });
    }

    public BallRenderer initialize(float yLevel, float radius, long startTime, long endTime) {
        super.yLevel = yLevel;
        super.radius = radius;
        super.startTime = startTime;
        super.endTime = endTime;

        sphere = new Sphere("ball" + MathHelper.getUniqueID());

        Minecraft.getInstance().execute(() -> {
            EffectManager.add(sphere);
        });

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
