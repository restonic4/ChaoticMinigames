package com.chaotic_loom.chaotic_minigames.core.minigames.sweeper;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.*;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.packets.SpawnSpinningBar;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.ServerSpinningBar;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.SpinningBar;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.CreateSkySphere;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.DeleteSkySphere;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.ThreadHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Minigame
public class Sweeper extends GenericMinigame {
    private final Playlist music;

    private SpinningBar spinningBar;

    public static String skyId = "sky" + MathHelper.getUniqueID();

    private static final MapList<MapSpawn> spawns = createSpawns(
            new MapSpawn(56, 38, 34),
            new MapSpawn(49, 38, 35),
            new MapSpawn(42, 38, 37),
            new MapSpawn(37, 38, 42),
            new MapSpawn(35, 38, 49),
            new MapSpawn(34, 38, 56),
            new MapSpawn(35, 38, 63),
            new MapSpawn(37, 38, 70),
            new MapSpawn(42, 38, 75),
            new MapSpawn(49, 38, 77),
            new MapSpawn(56, 38, 78),
            new MapSpawn(63, 38, 77),
            new MapSpawn(70, 38, 75),
            new MapSpawn(75, 38, 70),
            new MapSpawn(77, 38, 63),
            new MapSpawn(78, 38, 56),
            new MapSpawn(77, 38, 49),
            new MapSpawn(75, 38, 42),
            new MapSpawn(70, 38, 37),
            new MapSpawn(63, 38, 35)
    );

    public Sweeper() {
        super(new MinigameSettings(
                "sweeper",
                1,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new SweeperMapData(
                                "classic_sweeper",
                                spawns
                        ).setCenter(new BlockPos(56, 38, 56))
                                .setTime(1000)
                                .setRain(false),


                        new SweeperMapData(
                                "nether_sweeper",
                                spawns
                        ).setCenter(new BlockPos(56, 38, 56))
                                .setTime(18000)
                                .setRain(false)
                                .executeOnLoad(() -> {
                                    CreateSkySphere.sendToAll(
                                            GameManager.getInstance().getServer(),
                                            skyId,
                                            CMSharedConstants.netherTopColor,
                                            CMSharedConstants.netherBottomColor,
                                            new Vector3f(56, 38, 56),
                                            100
                                    );
                                })
                                .executeOnUnLoad(() -> {
                                    DeleteSkySphere.sendToAll(
                                            GameManager.getInstance().getServer(),
                                            skyId
                                    );
                                }),


                        new SweeperMapData(
                                "end_sweeper",
                                spawns
                        ).setCenter(new BlockPos(56, 38, 56))
                                .setTime(18000)
                                .setRain(false)
                                .executeOnLoad(() -> {
                                    CreateSkySphere.sendToAll(
                                            GameManager.getInstance().getServer(),
                                            skyId,
                                            CMSharedConstants.endTopColor,
                                            CMSharedConstants.endBottomColor,
                                            new Vector3f(56, 38, 56),
                                            100
                                    );
                                })
                                .executeOnUnLoad(() -> {
                                    DeleteSkySphere.sendToAll(
                                            GameManager.getInstance().getServer(),
                                            skyId
                                    );
                                }),


                        new SweeperMapData(
                                "joined_sweeper",
                                createSpawns(
                                        new MapSpawn(56, 38, 38),
                                        new MapSpawn(69, 38, 44),
                                        new MapSpawn(74, 38, 56),
                                        new MapSpawn(68, 38, 69),
                                        new MapSpawn(56, 38, 74),
                                        new MapSpawn(43, 38, 68),
                                        new MapSpawn(38, 38, 56),
                                        new MapSpawn(44, 38, 43)
                                )
                        ).setCenter(new BlockPos(56, 38, 56))
                                .setSpins(20)
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
    }

    @Override
    public void onServerStart(PartyManager partyManager) {
        partyManager.teleportInOrder();
        partyManager.freezeAll();
        partyManager.loadMapWeather();

        startTickingOnServer();

        music.playRandom();

        SweeperMapData map = partyManager.getCurrentMapData(SweeperMapData.class);

        int totalDuration = 60000;
        int spins = map.getSpins();
        float radius = map.getBarRadius();

        long currentTime = System.currentTimeMillis();

        spinningBar = PoolManager.acquire(ServerSpinningBar.class).initialize(
                partyManager.getCurrentMapData(SweeperMapData.class).getCenter().getCenter().toVector3f(),
                radius,
                currentTime + 4000,
                currentTime + 4000 + totalDuration,
                spins
        );

        SpawnSpinningBar.sendToAll(
                partyManager.getServerLevel().getServer(),
                partyManager.getCurrentMapData(SweeperMapData.class).getCenter().getCenter().toVector3f(),
                radius,
                currentTime + 4000,
                currentTime + 4000 + totalDuration,
                spins,
                getSettings().getId() + MathHelper.getUniqueID()
        );

        ThreadHelper.sleep(2000);

        partyManager.unFreezeAll();

        partyManager.startCountDown(totalDuration / 1000, () -> {});

        stopTickingOnServer();

        spinningBar.markFinish();

        ThreadHelper.sleep(2000);

        partyManager.executeForAllInGame(this::awardPlayer);
        announceWinners();
    }

    @Override
    public void tick(ExecutionSide executionSide) {
        if (this.spinningBar != null) {
            this.spinningBar.tick();
        }

        if (executionSide == ExecutionSide.SERVER) {
            List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
            for (int i = players.size() - 1; i >= 0; i--) {
                ServerPlayer serverPlayer = players.get(i);

                float minHeight = GameManager.getInstance().getPartyManager().getCurrentMapData(SweeperMapData.class).getMinHeight();

                if (serverPlayer.position().y() <= minHeight) {
                    GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
                }
            }
        } else if (executionSide == ExecutionSide.CLIENT && KnownServerDataOnClient.partyState != PartyStatus.State.PLAYING) {
            if (this.spinningBar != null) {
                spinningBar.markFinish();
            }
        }
    }

    @Override
    public void serverCleanup() {
        if (this.spinningBar != null) {
            this.spinningBar.markFinish();
        }

        super.serverCleanup();

        this.spinningBar = null;
    }

    @Override
    public void clientCleanup() {
        if (this.spinningBar != null) {
            this.spinningBar.markFinish();
        }

        super.clientCleanup();

        this.spinningBar = null;
    }

    public void setClientSpinningBar(SpinningBar spinningBar) {
        this.spinningBar = spinningBar;
    }
}
