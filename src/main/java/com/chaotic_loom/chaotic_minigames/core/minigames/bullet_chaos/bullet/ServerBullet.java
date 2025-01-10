package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.MathHelper;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.List;

public class ServerBullet extends Bullet {
    private final float radius;

    public ServerBullet(float radius, long spawnedTime, long endTime, Vector3f spawnPoint, Vector3f endPoint) {
        super(spawnPoint, endPoint, spawnedTime, endTime);
        this.radius = radius;
    }

    public void tick() {
        super.tick();

        if (isFinished()) {
            return;
        }

        List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            ServerPlayer serverPlayer = players.get(i);

            if (MathHelper.isSphereCollidingWithAABB(getPosition(), radius, MathHelper.getReducedPlayerAABB(serverPlayer, 0.5f))) {
                GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
            }
        }
    }
}
