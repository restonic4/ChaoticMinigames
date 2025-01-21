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
import com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off.packets.SendCameraTransform;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.cutscene.CutsceneAPI;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Minigame
public class KnockEmOff extends GenericMinigame {
    private final Playlist music;

    private ServerPlayer juggernaut;
    private static Vector3f currentCameraPos;
    private static Vector2f currentCameraRot;

    public KnockEmOff() {
        super(new MinigameSettings(
                "knock_em_off",
                2,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new KnockEmOffMap(
                                "template",
                                createSpawns(
                                    new MapSpawn(1, 1, 1)
                                ),
                                createSpawns(
                                    new MapSpawn(2, 2, 2)
                                )
                        ).setCameraPos(new Vector3f(1, 2, 5)).setCameraRot(new Vector2f(-133, 48))
                                .setTime(1000).setRain(false)
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
    }

    @Override
    public void onServerStart(PartyManager partyManager) {
        partyManager.loadMapWeather();

        music.playRandom();

        juggernaut = partyManager.getRandomInGamePlayer();
        partyManager.disqualifyPlayer(juggernaut, false);

        KnockEmOffMap map = partyManager.getCurrentMapData(KnockEmOffMap.class);
        SendCameraTransform.sendToAll(partyManager.getServerLevel().getServer(), map.getCameraPos(), map.getCameraRot());

        CMSharedConstants.LOGGER.info("Juggernaut: {}", juggernaut.getDisplayName());

        partyManager.teleportRandomly(map.getSpawns().get(0), juggernaut);
        partyManager.teleportRandomly(map.getSpawns().get(1), partyManager.getInGamePlayers());

        partyManager.freezeAll();

        int totalDuration = 60000;

        ThreadHelper.sleep(2000);

        partyManager.unFreezeAll();
        startTickingOnServer();

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

        partyManager.teleportLobby(juggernaut);
        partyManager.resetInventory(juggernaut);

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

    }

    @Override
    public void clientCleanup() {
        super.clientCleanup();

        CutsceneAPI.stop();
    }

    public static void setCurrentCameraPos(Vector3f newCurrentCameraPos) {
        currentCameraPos = newCurrentCameraPos;
    }

    public static void setCurrentCameraRot(Vector2f newCurrentCameraRot) {
        currentCameraRot = newCurrentCameraRot;
    }
}
