package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.*;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet.BulletManager;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet.BulletRenderer;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet.ServerBullet;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.packets.SpawnBullet;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.PlayMusic;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.util.EasingSystem;

import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Minigame
public class BulletChaos extends GenericMinigame {
    private final BulletManager<BulletRenderer> clientBulletManager;
    private final BulletManager<ServerBullet> serverBulletBulletManager;
    private final Playlist music;

    public BulletChaos() {
        super(new MinigameSettings(
                "bullet_chaos",
                1,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new BulletChaosMapData(
                                "bullet_chaos_old_tower",
                                createSpawns(
                                        new MapSpawn(new BlockPos(25, 41, 17)),
                                        new MapSpawn(new BlockPos(17, 41, 25)),
                                        new MapSpawn(new BlockPos(9, 41, 17)),
                                        new MapSpawn(new BlockPos(17, 41, 9)),
                                        new MapSpawn(new BlockPos(17, 41, 17))
                                )
                        ).setCenter(new BlockPos(17, 43, 17))
                         .setTime(1000)
                         .setRain(false)
                )
        ));

        this.clientBulletManager = new BulletManager<>();
        this.serverBulletBulletManager = new BulletManager<>();

        this.music = new Playlist();

        this.music.addMusic(SoundRegistry.CHOP_CHOP);
        this.music.addMusic(SoundRegistry.AGILE_ACCELERANDO);
        this.music.addMusic(SoundRegistry.BREAKNECK_BOOGIE);
    }

    @Override
    public void onStart(PartyManager partyManager) {
        partyManager.teleportRandomly();
        partyManager.loadMapWeather();

        startTickingOnServer();

        music.playRandom();

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

        ThreadHelper.runAsync(() -> {
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

            ThreadHelper.sleep(spawnDelay);

            for (int i = 0; i < bulletsPerTick; i++) {
                spawnBullet(partyManager, reachTime, radius);
            }
        }

        ThreadHelper.sleep(8000);

        stopTickingOnServer();

        partyManager.executeForAllInGame(this::awardPlayer);
        announceWinners();
    }

    public void tick(ExecutionSide executionSide) {
        if (executionSide == ExecutionSide.CLIENT) {
            clientBulletManager.tick();
        } else if (executionSide == ExecutionSide.SERVER) {
            serverBulletBulletManager.tick();

            List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
            for (int i = players.size() - 1; i >= 0; i--) {
                ServerPlayer serverPlayer = players.get(i);

                BlockPos center = GameManager.getInstance().getPartyManager().getCurrentMapData(BulletChaosMapData.class).getCenter();

                float xDistance = (float) serverPlayer.position().x - center.getX();
                float yDistance = (float) serverPlayer.position().y - center.getY();
                float zDistance = (float) serverPlayer.position().z - center.getZ();

                float distanceSquared = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;
                float requiredDistance = 20;

                if (distanceSquared > requiredDistance * requiredDistance) {
                    GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
                }
            }
        }
    }

    private void spawnBullet(PartyManager partyManager, int reachTime, float sphereRadius) {
        float radius = 100;

        BlockPos center = ((BulletChaosMapData) partyManager.getCurrentMapData()).getCenter();
        Vector3f randomPoint = MathHelper.getRandomPointOnCircle(center.getCenter().toVector3f(), radius);

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
        serverBulletBulletManager.addBullet(new ServerBullet(sphereRadius, currentTime, currentTime + reachTime, offsetStartPoint, offsetEndPoint));
    }

    public BulletManager<BulletRenderer> getClientBulletManager() {
        return clientBulletManager;
    }
}
