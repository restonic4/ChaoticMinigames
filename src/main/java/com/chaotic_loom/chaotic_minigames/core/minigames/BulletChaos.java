package com.chaotic_loom.chaotic_minigames.core.minigames;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import com.chaotic_loom.chaotic_minigames.core.data.minigames.bullet_chaos.ClientBullet;
import com.chaotic_loom.chaotic_minigames.core.data.minigames.bullet_chaos.ServerBullet;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.PlayMusic;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.SpawnBullet;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.ThreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Minigame
public class BulletChaos extends GenericMinigame {
    private static final List<ServerBullet> serverBullets = new ArrayList<>();

    public BulletChaos() {
        super(new MinigameSettings(
                "bullet_chaos",
                1,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new CustomMap(
                                "bullet_chaos_old_tower",
                                createSpawns(
                                        new MapSpawn(new BlockPos(25, 41, 17)),
                                        new MapSpawn(new BlockPos(17, 41, 25)),
                                        new MapSpawn(new BlockPos(9, 41, 17)),
                                        new MapSpawn(new BlockPos(17, 41, 9)),
                                        new MapSpawn(new BlockPos(17, 41, 17))
                                )
                        ).setCenter(new BlockPos(17, 43, 17))
                )
        ));
    }

    @Override
    public void onStart(PartyManager partyManager) {
        partyManager.teleportRandomly();

        PlayMusic.sendToAll(partyManager.getServerLevel().getServer(), SoundRegistry.CHOP_CHOP, 2000, EasingSystem.EasingType.LINEAR);

        AtomicBoolean runningCountDown = new AtomicBoolean(true);

        int startDelay = 4000;
        int endDelay = 1500;
        int totalDuration = 60000;

        int initialReachTime = 20000;
        int finalReachTime = 12000;

        int initialRadius = 1;
        int finalRadius = 3;

        int minBulletsPerTick = 2;
        int maxBulletsPerTick = 4;

        partyManager.runAsync(() -> {
            partyManager.startCountDown(totalDuration / 1000, () -> {
                runningCountDown.set(false);
            });
        });

        long startTime = System.currentTimeMillis();

        while (runningCountDown.get()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int spawnDelay = (int) (startDelay - ((startDelay - endDelay) / (double) totalDuration) * elapsedTime);

            spawnDelay = Math.max(spawnDelay, endDelay);

            int reachTime = (int) (initialReachTime - ((initialReachTime - finalReachTime) / (double) totalDuration) * elapsedTime);
            reachTime = Math.max(reachTime, finalReachTime);

            int radius = (int) (initialRadius - ((initialRadius - finalRadius) / (double) totalDuration) * elapsedTime);
            radius = Math.min(radius, finalRadius);

            int bulletsPerTick = (int) (minBulletsPerTick + ((maxBulletsPerTick - minBulletsPerTick) / (double) totalDuration) * elapsedTime);
            bulletsPerTick = Math.min(bulletsPerTick, maxBulletsPerTick);

            ThreadUtils.sleep(spawnDelay);

            for (int i = 0; i < bulletsPerTick; i++) {
                spawnBullet(partyManager, reachTime, radius);
            }
        }

        ThreadUtils.sleep(5000);

        partyManager.executeForAllInGame(this::awardPlayer);
    }

    private void spawnBullet(PartyManager partyManager, int reachTime, float sphereRadius) {
        float radius = 100;

        BlockPos center = ((CustomMap) partyManager.getCurrentMapData()).getCenter();
        Vector3f randomPoint = Util.getRandomPointOnCircle(center.getCenter().toVector3f(), radius);

        System.out.println("Spawning bullet at " + randomPoint);

        Vector3f direction = new Vector3f(center.getX() - randomPoint.x, center.getY() - randomPoint.y, center.getZ() - randomPoint.z);
        direction.normalize();

        Vector3f perpendicular = new Vector3f(-direction.z, 0, direction.x);
        perpendicular.normalize();

        float randomOffset = (float) (Math.random() * 20 - 10);
        perpendicular.mul(randomOffset);

        Vector3f offsetStartPoint = new Vector3f(randomPoint).add(perpendicular);
        Vector3f offsetEndPoint = new Vector3f(offsetStartPoint).add(direction.mul(radius * 2));

        long currentTime = System.currentTimeMillis();

        SpawnBullet.sendToAll(partyManager.getServerLevel().getServer(), offsetStartPoint, offsetEndPoint, currentTime, currentTime + reachTime, sphereRadius);
        addBullet(new ServerBullet(sphereRadius, currentTime, currentTime + reachTime, offsetStartPoint, offsetEndPoint));
    }

    public static class CustomMap extends MapData {
        private BlockPos center;

        public CustomMap(String structureId, MapList<MapSpawn> spawns) {
            super(structureId, spawns);
        }

        public BlockPos getCenter() {
            return center;
        }

        public CustomMap setCenter(BlockPos center) {
            this.center = center;
            return this;
        }
    }

    public static void tick() {
        for (ServerBullet clientBullet : serverBullets) {
            clientBullet.tick();
        }

        for (int i = serverBullets.size() - 1; i >= 0; i--) {
            ServerBullet clientBullet = serverBullets.get(i);

            if (clientBullet.isFinished()) {
                serverBullets.remove(i);
            } else {
                clientBullet.tick();
            }
        }
    }

    public static void addBullet(ServerBullet clientBullet) {
        serverBullets.add(clientBullet);
    }
}
