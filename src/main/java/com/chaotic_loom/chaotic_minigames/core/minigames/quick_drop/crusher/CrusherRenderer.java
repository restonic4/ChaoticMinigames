package com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.crusher;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.SpinningBarRenderer;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.under_control.client.rendering.effects.Cube;
import com.chaotic_loom.under_control.client.rendering.effects.Cylinder;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import java.util.List;

public class CrusherRenderer extends Crusher implements Poolable {
    private Cube cube;

    public CrusherRenderer() {
        super(new Vector3f(), 160, 0, 1);
    }

    public CrusherRenderer(Cube cube, Vector3f position, float startHeight, long startTime, long endTime) {
        super(position, startHeight, startTime, endTime);
        this.cube = cube;
    }

    public CrusherRenderer initialize(Cube cube, Vector3f position, float startHeight, long startTime, long endTime) {
        super.position = position;
        super.startHeight = startHeight;
        super.startTime = startTime;
        super.endTime = endTime;

        this.cube = cube;

        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            EffectManager.delete(cube.getId());
            return;
        }

        cube.setPosition(position);
    }

    @Override
    public void markFinish() {
        super.markFinish();
        PoolManager.release(this);
    }
}
