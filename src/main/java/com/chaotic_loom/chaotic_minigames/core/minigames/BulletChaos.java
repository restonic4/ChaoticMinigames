package com.chaotic_loom.chaotic_minigames.core.minigames;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import com.chaotic_loom.under_control.util.ThreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicBoolean;

@Minigame
public class BulletChaos extends GenericMinigame {
    public BulletChaos() {
        super(new MinigameSettings(
                "bullet_chaos",
                1,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new CustomMap(
                                "test",
                                createSpawns(
                                        new MapSpawn(new BlockPos(0, 0, 0)),
                                        new MapSpawn(new BlockPos(10, 0, 0))
                                )
                        ).setCenter(new BlockPos(60, 10, 60))
                )
        ));
    }

    @Override
    public void onStart(PartyManager partyManager) {
        partyManager.teleportRandomly();

        AtomicBoolean runningCountDown = new AtomicBoolean(true);

        int startDelay = 5000;
        int endDelay = 1000;
        int totalDuration = 60000;

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

            ThreadUtils.sleep(spawnDelay);
            spawnBullet(partyManager);
        }

        ThreadUtils.sleep(5000);

        partyManager.executeForAllInGame(this::awardPlayer);
    }

    private void spawnBullet(PartyManager partyManager) {
        BlockPos center = ((CustomMap) partyManager.getCurrentMapData()).getCenter();
        Vector3f randomPoint = Util.getRandomPointOnCircle(center.getCenter().toVector3f(), 50);

        System.out.println("Spawning bullet at " + randomPoint);

        Chicken chicken = EntityType.CHICKEN.create(partyManager.getServerLevel());

        chicken.setPos(randomPoint.x, randomPoint.y, randomPoint.z);
        chicken.setNoAi(true);
        chicken.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0, false, false));

        Vector3f direction = new Vector3f(center.getX() - randomPoint.x, center.getY() - randomPoint.y, center.getZ() - randomPoint.z);
        direction.normalize();

        float speed = 0.1f;
        chicken.setDeltaMovement(direction.x() * speed, direction.y() * speed, direction.z() * speed);

        partyManager.getServerLevel().addFreshEntity(chicken);
    }

    public static class CustomMap extends MapData {
        private BlockPos center;

        public CustomMap(String strucutreId, MapList<MapSpawn> spawns) {
            super(strucutreId, spawns);
        }

        public BlockPos getCenter() {
            return center;
        }

        public CustomMap setCenter(BlockPos center) {
            this.center = center;
            return this;
        }
    }
}
