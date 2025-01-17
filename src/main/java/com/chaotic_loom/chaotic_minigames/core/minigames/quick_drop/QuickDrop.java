package com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.*;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.crusher.Crusher;
import com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.crusher.ServerCrusher;
import com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.packets.SpawnCrusher;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.SweeperMapData;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.packets.SpawnSpinningBar;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.ServerSpinningBar;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.SpinningBar;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.ThreadHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.List;

@Minigame
public class QuickDrop extends GenericMinigame {
    private final Playlist music;

    private Crusher crusher;

    public QuickDrop() {
        super(new MinigameSettings(
                "quick_drop",
                1,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new QuickDropMapData(
                                "demo_world",
                                createSpawns(
                                    new MapSpawn(2, 33, 2)
                                )
                        ).setStartingHeight(41).setTime(1000).setRain(false)
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
    }

    @Override
    public void onStart(PartyManager partyManager) {
        partyManager.teleportInOrder();
        partyManager.freezeAll();
        partyManager.loadMapWeather();

        startTickingOnServer();

        music.playRandom();

        QuickDropMapData map = partyManager.getCurrentMapData(QuickDropMapData.class);

        int totalDuration = 40000;
        float startingHeight = map.getStartingHeight();

        long currentTime = System.currentTimeMillis();

        crusher = PoolManager.acquire(ServerCrusher.class).initialize(
                new Vector3f(),
                startingHeight,
                currentTime + 4000,
                currentTime + 4000 + totalDuration
        );

        SpawnCrusher.sendToAll(
                partyManager.getServerLevel().getServer(),
                new Vector3f(),
                startingHeight,
                currentTime + 4000,
                currentTime + 4000 + totalDuration,
                getSettings().getId() + MathHelper.getUniqueID()
        );

        ThreadHelper.sleep(2000);

        partyManager.unFreezeAll();

        partyManager.startCountDown(totalDuration / 1000, () -> {});

        stopTickingOnServer();

        crusher.markFinish();

        ThreadHelper.sleep(2000);

        announceWinners();
    }

    @Override
    public void tick(ExecutionSide executionSide) {
        if (this.crusher != null) {
            this.crusher.tick();
        }

        if (executionSide == ExecutionSide.SERVER) {
            List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
            for (int i = players.size() - 1; i >= 0; i--) {
                ServerPlayer serverPlayer = players.get(i);

                if (serverPlayer.position().y() <= 0) {
                    GameManager.getInstance().getPartyManager().teleportLobby(serverPlayer);
                    awardPlayer(serverPlayer);
                }
            }
        } else if (executionSide == ExecutionSide.CLIENT && KnownServerDataOnClient.partyState != PartyStatus.State.PLAYING) {
            if (this.crusher != null) {
                crusher.markFinish();
            }
        }
    }

    @Override
    public void serverCleanup() {
        if (this.crusher != null) {
            this.crusher.markFinish();
        }

        super.serverCleanup();

        this.crusher = null;
    }

    @Override
    public void clientCleanup() {
        if (this.crusher != null) {
            this.crusher.markFinish();
        }

        super.clientCleanup();

        this.crusher = null;
    }

    public void setClientCrusher(Crusher crusher) {
        this.crusher = crusher;
    }
}
