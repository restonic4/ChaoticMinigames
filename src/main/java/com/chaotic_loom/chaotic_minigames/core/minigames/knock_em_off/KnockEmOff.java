package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import com.chaotic_loom.chaotic_minigames.core.data.MultipleSpawnerMapData;
import com.chaotic_loom.chaotic_minigames.core.data.Playlist;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.epidemic_rush.packets.UpdateZombieData;
import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.ball.Ball;
import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.packets.SendCameraTransform;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.cutscene.CutsceneAPI;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.events.types.ServerPlayerExtraEvents;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Minigame
public class KnockEmOff extends GenericMinigame {
    private final Playlist music;

    private ServerPlayer juggernaut;
    private Ball ball;

    private static Vector3f currentCameraPos;
    private static Vector2f currentCameraRot;
    private static boolean isLocalPlayerJuggernaut = false;

    public KnockEmOff() {
        super(new MinigameSettings(
                "knock_em_off",
                2,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new KnockEmOffMap(
                                "pool",
                                createSpawns(
                                    new MapSpawn(2, 2, 7)
                                ),
                                createSpawns(
                                    new MapSpawn(9, 2, 7)
                                )
                        ).setCameraPos(new Vector3f(1, 2, 5))
                         .setCameraRot(new Vector2f(48, -133))
                         .setPointA(new Vector3f(9, 2, 2))
                         .setPointB(new Vector3f(9, 2, 12))
                         .setTime(1000)
                         .setRain(false)
                )
        ));

        this.music = new Playlist();

        this.music.addMusic(SoundRegistry.CHOP_CHOP);
        this.music.addMusic(SoundRegistry.AGILE_ACCELERANDO);
        this.music.addMusic(SoundRegistry.BREAKNECK_BOOGIE);
        this.music.addMusic(SoundRegistry.DASHING_ON_THE_DOUBLE);
        this.music.addMusic(SoundRegistry.DOUBLE_TIME);
        this.music.addMusic(SoundRegistry.LICKETY_SPLIT);
        this.music.addMusic(SoundRegistry.NIMBLY_DOES_IT);
        this.music.addMusic(SoundRegistry.PRONTO);
        this.music.addMusic(SoundRegistry.SWIFT_DESCENT);
        this.music.addMusic(SoundRegistry.TIME_IS_OF_THE_ESSENCE);

        ServerPlayConnectionEvents.DISCONNECT.register((serverGamePacketListener, minecraftServer) -> {
            ServerPlayer serverPlayer = serverGamePacketListener.getPlayer();

            if (juggernaut != null && juggernaut.equals(serverPlayer)) {
                juggernaut = null;
            }
        });

        ServerPlayerExtraEvents.JUMP_KEY_PRESSED.register((serverPlayer) -> {
            if (canTickOnServer() && juggernaut.equals(serverPlayer)) {
                throwBall();
            }
        });
    }

    @Override
    public void onServerStart(PartyManager partyManager) {
        partyManager.loadMapWeather();

        music.playRandom();

        juggernaut = partyManager.getRandomInGamePlayer();
        partyManager.disqualifyPlayer(juggernaut, false);

        KnockEmOffMap map = partyManager.getCurrentMapData(KnockEmOffMap.class);
        SendCameraTransform.sendToAll(partyManager.getServerLevel().getServer(), juggernaut, map.getCameraPos(), map.getCameraRot());

        CMSharedConstants.LOGGER.info("Juggernaut: {}", juggernaut.getDisplayName());

        partyManager.teleportRandomly(map.getSpawns().get(0), juggernaut);
        partyManager.teleportRandomly(map.getSpawns().get(1), partyManager.getInGamePlayers());

        partyManager.freezeAll();

        int totalDuration = 60000;

        ThreadHelper.sleep(2000);

        partyManager.unFreezeAll();
        startTickingOnServer();

        partyManager.freezePlayer(juggernaut, juggernaut.position().toVector3f());

        partyManager.startCountDown(totalDuration / 1000, () -> {}, (timeLeft) -> {
            return juggernaut == null || partyManager.getInGamePlayers().isEmpty();
        });

        stopTickingOnServer();

        boolean winForJuggernaut = partyManager.getInGamePlayers().isEmpty();

        CMSharedConstants.LOGGER.info("Win for juggernaut: {}", winForJuggernaut);
        CMSharedConstants.LOGGER.info("Players: {}", partyManager.getInGamePlayers().size());

        if (winForJuggernaut) {
            if (juggernaut != null) {
                awardPlayer(juggernaut);
                announceWinners(juggernaut);
            } else {
                announceNoWinners();
            }
        } else {
            partyManager.executeForAllInGame(this::awardPlayer);
            announceWinners();
        }

        if (juggernaut != null) {
            partyManager.teleportLobby(juggernaut);
            partyManager.resetInventory(juggernaut);
        }

        juggernaut = null;
    }

    @Override
    public void onClientStart() {
        CutsceneAPI.setPosition(currentCameraPos);
        CutsceneAPI.setRotation(currentCameraRot);

        CutsceneAPI.play();
    }

    @Override
    public void tick(ExecutionSide executionSide) {
        if (executionSide == ExecutionSide.CLIENT && isLocalPlayerJuggernaut) {
            CutsceneAPI.stop();
        }
    }

    private void throwBall() {
        CMSharedConstants.LOGGER.info("Throwing ball");

        PartyManager partyManager = GameManager.getInstance().getPartyManager();
        KnockEmOffMap map = partyManager.getCurrentMapData(KnockEmOffMap.class);

        Vector3f playerPos = juggernaut.position().toVector3f();
        Vector3f direction = getFlatLookDirection(juggernaut);

        Vector3f pointA = map.getPointA();
        Vector3f pointB = map.getPointB();

        Vector3f impactPoint = calculateImpactPoint(playerPos, direction, pointA, pointB);
        Vector3f symmetricImpactPoint = getSymmetricPoint(pointA, pointB, impactPoint);


    }

    public static Vector3f getFlatLookDirection(ServerPlayer player) {
        double yaw = player.getYHeadRot();
        double yawRad = Math.toRadians(yaw);

        double x = -Math.sin(yawRad);
        double z = Math.cos(yawRad);

        return new Vector3f((float) x, (float) 0, (float) z).normalize();
    }

    public static Vector3f calculateImpactPoint(Vector3f playerPos, Vector3f direction, Vector3f pointA, Vector3f pointB) {
        Vector3f ab = new Vector3f(pointB).sub(pointA);

        float denominator = (direction.x * ab.z - direction.z * ab.x);

        if (denominator == 0) {
            return getClosestPoint(playerPos, pointA, pointB);
        }

        Vector3f diff = new Vector3f(playerPos).sub(pointA);

        float t = (diff.x * ab.z - diff.z * ab.x) / denominator;
        float u = (direction.x * diff.z - direction.z * diff.x) / denominator;

        if (u >= 0 && u <= 1) {
            return new Vector3f(pointA).add(new Vector3f(ab).mul(u));
        } else {
            return getClosestPoint(playerPos, pointA, pointB);
        }
    }

    private static Vector3f getClosestPoint(Vector3f playerPos, Vector3f pointA, Vector3f pointB) {
        float distanceToA = playerPos.distance(pointA);
        float distanceToB = playerPos.distance(pointB);
        return distanceToA <= distanceToB ? pointA : pointB;
    }

    public static Vector3f getSymmetricPoint(Vector3f A, Vector3f B, Vector3f P) {
        Vector3f midPoint = new Vector3f();
        A.add(B, midPoint);
        midPoint.mul(0.5f);

        Vector3f difference = new Vector3f();
        P.sub(midPoint, difference);

        Vector3f opposite = new Vector3f();
        midPoint.sub(difference, opposite);

        return opposite;
    }

    @Override
    public void clientCleanup() {
        super.clientCleanup();

        CutsceneAPI.stop();
        isLocalPlayerJuggernaut = false;
    }

    public static void setCurrentCameraPos(Vector3f newCurrentCameraPos) {
        currentCameraPos = newCurrentCameraPos;
    }

    public static void setCurrentCameraRot(Vector2f newCurrentCameraRot) {
        currentCameraRot = newCurrentCameraRot;
    }

    public static void setCurrentJuggernaut(boolean value) {
        isLocalPlayerJuggernaut = value;
    }
}
