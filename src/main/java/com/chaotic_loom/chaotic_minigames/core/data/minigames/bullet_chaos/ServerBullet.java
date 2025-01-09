package com.chaotic_loom.chaotic_minigames.core.data.minigames.bullet_chaos;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.under_control.client.rendering.effects.SphereManager;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.List;

public class ServerBullet {
    private final float radius;
    private final long spawnedTime, endTime;
    private final Vector3f spawnPoint, endPoint;

    boolean finished = false;

    public ServerBullet(float radius, long spawnedTime, long endTime, Vector3f spawnPoint, Vector3f endPoint) {
        this.radius = radius;
        this.spawnedTime = spawnedTime;
        this.endTime = endTime;
        this.spawnPoint = spawnPoint;
        this.endPoint = endPoint;
    }

    private final Vector3f cacheVec = new Vector3f();
    public void tick() {
        long currentTime = System.currentTimeMillis();

        if (currentTime >= endTime) {
            finished = true;
            return;
        }

        float xProgress = EasingSystem.getEasedValue(spawnedTime, endTime, spawnPoint.x, endPoint.x, EasingSystem.EasingType.LINEAR);
        float yProgress = EasingSystem.getEasedValue(spawnedTime, endTime, spawnPoint.y, endPoint.y, EasingSystem.EasingType.LINEAR);
        float zProgress = EasingSystem.getEasedValue(spawnedTime, endTime, spawnPoint.z, endPoint.z, EasingSystem.EasingType.LINEAR);

        cacheVec.set(xProgress, yProgress, zProgress);

        List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            ServerPlayer serverPlayer = players.get(i);

            if (Util.isSphereCollidingWithAABB(cacheVec, radius, Util.getReducedPlayerAABB(serverPlayer, 0.5f))) {
                GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
