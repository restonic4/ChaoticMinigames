package com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.crusher;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public abstract class Crusher implements Poolable {
    protected Vector3f position;
    protected float startHeight;
    protected long startTime;
    protected long endTime;
    protected boolean finished = false;

    public Crusher(Vector3f position, float startHeight, long startTime, long endTime) {
        this.position = position;
        this.startHeight = startHeight;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void tick() {
        long currentTime = GameManager.getInstance().getSynchronizationHelper().getCurrentTime();

        if (currentTime >= endTime) {
            finished = true;
            return;
        }

        float progress = EasingSystem.getEasedValue(currentTime, startTime, endTime, startHeight, 0, EasingSystem.EasingType.LINEAR);

        this.position.set(this.position.x, progress, this.position.z);
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinish() {
        this.finished = true;
    }

    @Override
    public void reset() {
        this.finished = false;
    }
}
