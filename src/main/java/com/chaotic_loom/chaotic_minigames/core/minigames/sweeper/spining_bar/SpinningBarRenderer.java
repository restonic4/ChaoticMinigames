package com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.under_control.client.rendering.effects.*;
import com.chaotic_loom.under_control.util.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import java.util.List;

public class SpinningBarRenderer extends SpinningBar {
    private final Cube cube;

    private Cylinder debugSphere1, debugSphere2;

    public SpinningBarRenderer(Cube cube, Vector3f position, float radius, long startTime, long endTime, int spins) {
        super(position, radius, startTime, endTime, spins);

        this.cube = cube;

        if (CMClientConstants.RENDER_HITBOX) {
            this.debugSphere1 = CylinderManager.create(MathHelper.getUniqueID());
            this.debugSphere2 = CylinderManager.create(MathHelper.getUniqueID());
        }
    }

    private final Vector3f cacheRotation = new Vector3f();
    private final Vector3f cacheScale = new Vector3f();
    private final Vector3f cacheDebugHitboxesScale = new Vector3f(hitbox_radius, HITBOX_HEIGHT, hitbox_radius);

    @Override
    Player getClosestPlayer(Vector3f collisionPoint) {
        List<AbstractClientPlayer> players = Minecraft.getInstance().level.players();

        AbstractClientPlayer foundAbstractClientPlayer = null;
        float foundAbstractClientPlayerDistance = 666;

        for (int i = players.size() - 1; i >= 0; i--) {
            AbstractClientPlayer abstractClientPlayer = players.get(i);

            float currentDistance = Util.calculateDistance(collisionPoint, abstractClientPlayer.position());

            if (currentDistance <= foundAbstractClientPlayerDistance) {
                foundAbstractClientPlayer = abstractClientPlayer;
                foundAbstractClientPlayerDistance = currentDistance;
            }
        }

        return foundAbstractClientPlayer;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            CubeManager.delete(cube.getId());

            if (debugSphere1 != null && debugSphere2 != null) {
                CylinderManager.delete(debugSphere1.getId());
                CylinderManager.delete(debugSphere2.getId());
            }

            return;
        }

        cacheRotation.set(0, getCurrentAngle(), 0);
        cacheScale.set(radius * 2, 0.3f, 0.3f);

        cube.setPosition(position);
        cube.setScale(cacheScale);
        cube.setRotation(cacheRotation);

        if (CMClientConstants.RENDER_HITBOX) {
            cacheDebugHitboxesScale.set(hitbox_radius, HITBOX_HEIGHT, hitbox_radius);

            debugSphere1.setPosition(getPosition1());
            debugSphere1.setScale(cacheDebugHitboxesScale);

            debugSphere2.setPosition(getPosition2());
            debugSphere2.setScale(cacheDebugHitboxesScale);
        }
    }
}
