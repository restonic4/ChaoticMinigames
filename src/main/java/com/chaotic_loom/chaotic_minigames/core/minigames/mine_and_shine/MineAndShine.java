package com.chaotic_loom.chaotic_minigames.core.minigames.mine_and_shine;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.PartyManager;
import com.chaotic_loom.chaotic_minigames.core.data.*;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.mine_and_shine.packets.ShowEmeralds;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.under_control.client.rendering.RenderingHelper;
import com.chaotic_loom.under_control.client.rendering.effects.Cube;
import com.chaotic_loom.under_control.client.rendering.effects.EffectManager;
import com.chaotic_loom.under_control.client.rendering.effects.RenderableEffect;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.util.ThreadHelper;
import com.chaotic_loom.under_control.util.data_holders.RenderingFlags;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.chaotic_loom.chaotic_minigames.core.PartyManager.AREA_RANGE;
import static com.chaotic_loom.chaotic_minigames.core.PartyManager.MIN_HEIGHT;

@Minigame
public class MineAndShine extends GenericMinigame {
    private final Playlist music;

    private ServerPlayer first, second, third;

    private static List<BlockPos> emeraldsList = new ArrayList<>();

    public MineAndShine() {
        super(new MinigameSettings(
                "mine_and_shine",
                1,
                GENERIC_MAX_PLAYERS,
                createMaps(
                        new SpawnerMapData(
                                "just_stone",
                                createSpawns(
                                    new MapSpawn(10, 3, 10)
                                )
                        ).executeOnLoad(() -> {
                            replaceStoneWithEmeraldOre(GameManager.getInstance().getPartyManager().getServerLevel());
                        }).setTime(1000).setRain(false),


                        new SpawnerMapData(
                                "lonely_island",
                                createSpawns(
                                        new MapSpawn(6, 14, 10)
                                )
                        ).executeOnLoad(() -> {
                            replaceStoneWithEmeraldOre(GameManager.getInstance().getPartyManager().getServerLevel());
                        }).setTime(1000).setRain(false)
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

        PlayerBlockBreakEvents.BEFORE.register((level, player, blockPos, blockState, blockEntity) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                onBlockBroken(serverPlayer, blockState);
            }

            return true;
        });
    }

    @Override
    public void onStart(PartyManager partyManager) {
        partyManager.teleportInOrder();
        partyManager.freezeAll();
        partyManager.loadMapWeather();

        startTickingOnServer();

        music.playRandom();

        setInventories();

        int totalDuration = 40000;

        ThreadHelper.sleep(2000);

        partyManager.unFreezeAll();

        partyManager.startCountDown(totalDuration / 1000, () -> {}, (timeLeft) -> {
            if (timeLeft <= 10) {
                ShowEmeralds.sendToAll(GameManager.getInstance().getServer(), emeraldsList);
            }

            return GameManager.getInstance().getPartyManager().getInGamePlayers().isEmpty();
        });

        stopTickingOnServer();

        if (third != null && second != null && first != null) {
            announceWinners(first, second, third);
        } else if (third == null && second != null && first != null) {
            announceWinners(first, second);
        } else if (third == null && second == null && first != null) {
            announceWinners(first);
        } else {
            announceNoWinners();
        }

        first = null;
        second = null;
        third = null;
        emeraldsList.clear();
    }

    @Override
    public void tick(ExecutionSide executionSide) {
        if (executionSide == ExecutionSide.SERVER) {
            if (first != null && second != null && third != null) {
                for (ServerPlayer serverPlayer : GameManager.getInstance().getPartyManager().getInGamePlayers()) {
                    GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
                }
            }
        } else if (executionSide == ExecutionSide.CLIENT) {
            for (int i = 0; i < emeraldsList.size(); i++) {
                RenderableEffect renderableEffect = EffectManager.get("mineOre" + i);

                if (renderableEffect == null) {
                    renderableEffect = new Cube("mineOre" + i);
                    EffectManager.add(renderableEffect);

                    renderableEffect.setScale(1, 1, 1);
                    renderableEffect.setColor(new Color(0x8000FF00, true));
                    renderableEffect.setRenderingFlags(RenderingFlags.ON_TOP);
                }

                BlockPos pos = emeraldsList.get(i);

                renderableEffect.setPosition(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
            }
        }
    }

    public void setInventories() {
        PartyManager partyManager = GameManager.getInstanceOrCreate().getPartyManager();

        for (ServerPlayer serverPlayer : partyManager.getInGamePlayers()) {
            serverPlayer.getInventory().setItem(0, new ItemStack(Items.IRON_PICKAXE));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60 * 20, 3, false, false, false));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 60 * 20, 0, false, false, false));
        }
    }

    public void onBlockBroken(ServerPlayer serverPlayer, BlockState blockState) {
        if (blockState.is(Blocks.EMERALD_ORE) && (first == null || second == null || third == null)) {
            GameManager.getInstance().getPartyManager().teleportLobby(serverPlayer);
            GameManager.getInstance().getPartyManager().resetInventory(serverPlayer);
            awardPlayer(serverPlayer, true);

            if (first == null) {
                first = serverPlayer;
            } else if (second == null) {
                second = serverPlayer;
            } else if (third == null) {
                third = serverPlayer;
            }
        }
    }

    public static void replaceStoneWithEmeraldOre(ServerLevel serverLevel) {
        List<BlockPos> stoneBlocks = new ArrayList<>();

        for (int x = -1; x <= AREA_RANGE + 1; x++) {
            for (int y = MIN_HEIGHT; y <= AREA_RANGE + 1; y++) {
                for (int z = -1; z <= AREA_RANGE + 1; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (serverLevel.getBlockState(pos).is(Blocks.STONE)) {
                        stoneBlocks.add(pos);
                    }
                }
            }
        }

        int emeralds = 5;

        if (stoneBlocks.size() < emeralds) {
            System.out.println("Not enough blocks.");
            return;
        }

        Random random = new Random();
        for (int i = 0; i < emeralds; i++) {
            int randomIndex = random.nextInt(stoneBlocks.size());
            BlockPos chosenPos = stoneBlocks.remove(randomIndex);
            serverLevel.setBlock(chosenPos, Blocks.EMERALD_ORE.defaultBlockState(), 3);
            emeraldsList.add(chosenPos);
        }
    }

    @Override
    public void clientCleanup() {
        for (int i = 0; i < emeraldsList.size(); i++) {
            RenderableEffect renderableEffect = EffectManager.get("mineOre" + i);

            if (renderableEffect != null) {
                EffectManager.delete("mineOre" + i);
            }
        }

        emeraldsList.clear();
    }

    public static void setEmeraldsList(List<BlockPos> newEmeraldsList) {
        emeraldsList = newEmeraldsList;
    }
}
