package com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.List;

public class ServerSpinningBar extends SpinningBar implements Poolable {
    public ServerSpinningBar() {
        super(new Vector3f(), 25, 0, 1, 1);
    }

    public ServerSpinningBar(Vector3f position, float radius, long startTime, long endTime, int spins) {
        super(position, radius, startTime, endTime, spins);
    }

    public ServerSpinningBar initialize(Vector3f position, float radius, long startTime, long endTime, int spins) {
        super.position = position;
        super.radius = radius;
        super.startTime = startTime;
        super.endTime = endTime;
        super.spins = spins;

        return this;
    }

    @Override
    public Player getClosestPlayer(Vector3f collisionPoint) {
        List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();

        ServerPlayer foundServerPlayer = null;
        float foundServerPlayerDistance = 666;

        for (int i = players.size() - 1; i >= 0; i--) {
            ServerPlayer serverPlayer = players.get(i);

            float currentDistance = Util.calculateDistance(collisionPoint, serverPlayer.position());

            if (currentDistance <= foundServerPlayerDistance) {
                foundServerPlayer = serverPlayer;
                foundServerPlayerDistance = currentDistance;
            }
        }

        return foundServerPlayer;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            return;
        }

        List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            ServerPlayer serverPlayer = players.get(i);

            AABB playerAABB = MathHelper.getReducedPlayerAABB(serverPlayer, 0.5f);


            if (MathHelper.isCylinderCollidingWithAABB(getPosition1(), hitbox_radius, HITBOX_HEIGHT, playerAABB) || MathHelper.isCylinderCollidingWithAABB(getPosition2(), hitbox_radius, HITBOX_HEIGHT, playerAABB)) {
                GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
            }
        }
    }

    @Override
    public void markFinish() {
        super.markFinish();
        PoolManager.release(this);
    }
}
