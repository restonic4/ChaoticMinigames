package com.chaotic_loom.chaotic_minigames.core.minigames.epidemic_rush;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.*;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.epidemic_rush.packets.UpdateZombieData;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.chaotic_loom.chaotic_minigames.core.PartyManager.AREA_RANGE;
import static com.chaotic_loom.chaotic_minigames.core.PartyManager.MIN_HEIGHT;

@Minigame
public class EpidemicRush extends GenericMinigame {
    private final Playlist music;

    private ServerPlayer mainZombie;
    private List<ServerPlayer> zombies = new ArrayList<>();

    public EpidemicRush() {
        super(new MinigameSettings(
                "epidemic_rush",
                2,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new MultipleSpawnerMapData(
                                "city",
                                createSpawns(
                                        new MapSpawn(150, 2, 17),
                                        new MapSpawn(51, 3, 126)
                                ),
                                createSpawns(
                                        new MapSpawn(17, 2, 67),
                                        new MapSpawn(49, 2, 46),
                                        new MapSpawn(72, 2, 17),
                                        new MapSpawn(110, 2, 66),
                                        new MapSpawn(70, 3, 83),
                                        new MapSpawn(110, 2, 103),
                                        new MapSpawn(79, 3, 41)
                                )
                        ).setTime(18000).setRain(false)
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

            if (mainZombie != null && mainZombie.equals(serverPlayer)) {
                zombies.remove(mainZombie);
                mainZombie = null;
            }
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((livingEntity, damageSource, amount) -> {
            if (!canTickOnServer()) {
                return true;
            }

            if (livingEntity instanceof ServerPlayer hurtPlayer && damageSource.getEntity() != null && damageSource.getEntity() instanceof ServerPlayer agroPlayer) {
                if (agroPlayer.equals(mainZombie)) {
                    infectPlayer(hurtPlayer);
                    return false;
                }

                for (ServerPlayer zombie : zombies) {
                    if (zombie.equals(agroPlayer)) {
                        infectPlayer(hurtPlayer);
                        return false;
                    }
                }
            }

            return false;
        });
    }

    @Override
    public void onStart(PartyManager partyManager) {
        partyManager.loadMapWeather();

        music.playRandom();

        mainZombie = partyManager.getRandomInGamePlayer();
        zombies.add(mainZombie);
        partyManager.disqualifyPlayer(mainZombie, false);
        UpdateZombieData.sendToAll(GameManager.getInstance().getServer(), zombies);

        MultipleSpawnerMapData map = partyManager.getCurrentMapData(MultipleSpawnerMapData.class);

        CMSharedConstants.LOGGER.info("Main zombie: {}", mainZombie.getDisplayName());

        partyManager.teleportRandomly(map.getSpawns().get(0), zombies);
        partyManager.teleportRandomly(map.getSpawns().get(1), partyManager.getInGamePlayers());

        partyManager.freezeAll();

        setInventories();

        int totalDuration = 120000;

        ThreadHelper.sleep(2000);

        partyManager.unFreezeAll();
        partyManager.freezePlayer(mainZombie, mainZombie.position().toVector3f());
        startTickingOnServer();
        partyManager.allowDamage();

        ThreadHelper.runAsync(() -> {
            ThreadHelper.sleep(5000);

            partyManager.unFreezeAll();
        });

        partyManager.startCountDown(totalDuration / 1000, () -> {}, (timeLeft) -> {
            return zombies.isEmpty() || partyManager.getInGamePlayers().isEmpty();
        });

        stopTickingOnServer();

        boolean winForZombies = partyManager.getInGamePlayers().isEmpty();

        CMSharedConstants.LOGGER.info("Win for zombies: {}", winForZombies);
        CMSharedConstants.LOGGER.info("Zombies: {}", zombies.size());
        CMSharedConstants.LOGGER.info("Players: {}", partyManager.getInGamePlayers().size());

        if (winForZombies) {
            if (mainZombie != null) {
                awardPlayer(mainZombie);
                announceWinners(mainZombie);
            } else {
                announceNoWinners();
            }
        } else {
            partyManager.executeForAllInGame(this::awardPlayer);
            announceWinners();
        }

        for (ServerPlayer zombie : zombies) {
            partyManager.teleportLobby(zombie);
            partyManager.resetInventory(zombie);
        }

        mainZombie = null;
        zombies.clear();
        UpdateZombieData.sendToAll(GameManager.getInstance().getServer(), zombies);
    }

    @Override
    public void tick(ExecutionSide executionSide) {

    }

    public void infectPlayer(ServerPlayer serverPlayer) {
        GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer, false);
        zombies.add(serverPlayer);
        UpdateZombieData.sendToAll(GameManager.getInstance().getServer(), zombies);
    }

    public void setInventories() {
        PartyManager partyManager = GameManager.getInstanceOrCreate().getPartyManager();

        mainZombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 65 * 20, 2, false, false, false));
        mainZombie.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 65 * 20, 0, false, false, false));
        mainZombie.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 10 * 20, 0, false, false, false));
    }
}
