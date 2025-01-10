package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.PlayMusic;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PartyManager {
    private final GameManager gameManager;
    private final PartyStatus partyStatus;
    private final ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);
    private GenericMinigame currentMinigame;
    private MapData currentMapData;
    private List<ServerPlayer> inGamePlayers = new ArrayList<>();

    private boolean hasMapBeenLoaded = false;

    public PartyManager() {
        this.gameManager = GameManager.getInstance();
        this.partyStatus = new PartyStatus();
    }

    public void onStart() {
        ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);
        serverLevel.setDefaultSpawnPos(new BlockPos(0, 0,0), 10);

        loop();
    }

    public void loop() {
        while (true) {
            onBeforeVote();
            onVote();
            onAfterVote();
            onPlaying();
            onAfterPlaying();
        }
    }

    private void onBeforeVote() {
        System.out.println("Before vote");

        partyStatus.setState(PartyStatus.State.BEFORE_VOTING_INTERMISSION);

        startCountDown();
    }

    private void onVote() {
        System.out.println("Vote");

        partyStatus.setState(PartyStatus.State.VOTING);

        startCountDown();
    }

    private void onAfterVote() {
        System.out.println("After vote");

        partyStatus.setState(PartyStatus.State.AFTER_VOTING_INTERMISSION);

        currentMinigame = MinigameRegistry.MINIGAMES.get(0);
        currentMapData = currentMinigame.getSettings().getMaps().getRandom();

        loadMap(serverLevel, currentMapData.getStrucutreId());

        startCountDown();

        while (!hasMapBeenLoaded) {
            gameManager.sendSubtitleToPlayers(Component.literal("Waiting for map..."));
            ThreadHelper.sleep(1000);
        }
    }

    private void onPlaying() {
        System.out.println("Playing");

        partyStatus.setState(PartyStatus.State.PLAYING);

        inGamePlayers.addAll(serverLevel.getServer().getPlayerList().getPlayers());
        currentMinigame.onStart(this);
    }

    private void onAfterPlaying() {
        System.out.println("After playing");

        PlayMusic.sendToAll(getServerLevel().getServer(), SoundRegistry.MUSIC_MAIN_MENU_1, 2000, EasingSystem.EasingType.LINEAR);

        unLoadMap(serverLevel);
        currentMapData = null;
        currentMinigame = null;
        inGamePlayers.clear();
    }

    public void teleportRandomly() {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            BlockPos spawnPos = currentMapData.getSpawns().getRandom().getBlockPos();
            serverPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        }
    }

    private void loadMap(ServerLevel serverLevel, String structureName) {
        hasMapBeenLoaded = false;

        serverLevel.getServer().execute(() -> {
            StructureTemplateManager structureManager = serverLevel.getStructureManager();

            BlockPos position = new BlockPos(0, 0, 0);

            ResourceLocation structureId = new ResourceLocation(CMSharedConstants.ID, structureName);
            StructureTemplate structure = structureManager.get(structureId).orElse(null);

            if (structure == null) {
                GameManager.LOGGER.error("Could not place the structure: {}", structureName);
                return;
            }

            StructurePlaceSettings placementData = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false);

            structure.placeInWorld(serverLevel, position, position, placementData, serverLevel.getRandom(), 2);

            GameManager.LOGGER.info("Structure {} placed on {}", structureName, position);

            hasMapBeenLoaded = true;
        });
    }

    private void unLoadMap(ServerLevel serverLevel) {
        serverLevel.getServer().execute(() -> {
            AABB area = new AABB(0, 0, 0, 160, 160, 160);
            List<Entity> entities = serverLevel.getEntities(null, area);

            for (Entity entity : entities) {
                if (!(entity instanceof Player)) {
                    entity.discard();
                }
            }

            for (int x = 0; x <= 160; x++) {
                for (int y = 0; y <= 160; y++) {
                    for (int z = 0; z <= 160; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        });

        GameManager.LOGGER.info("Map unloaded");
    }

    public void startCountDown() {
        startCountDown(getCountDownTime(partyStatus.getState()), null);
    }

    public void startCountDown(int seconds, Runnable runnable) {
        ThreadHelper.runCountDown(seconds, runnable, (timeLeft) -> {
            gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
        });
    }

    public void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

    public void executeForAllInGame(Consumer<ServerPlayer> consumer) {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            consumer.accept(serverPlayer);
        }
    }

    public int getCountDownTime(PartyStatus.State state) {
        if (state == PartyStatus.State.BEFORE_VOTING_INTERMISSION) {
            return CMSharedConstants.BEFORE_VOTE_TIME;
        } else if (state == PartyStatus.State.VOTING) {
            return CMSharedConstants.VOTE_TIME;
        } else if (state == PartyStatus.State.AFTER_VOTING_INTERMISSION) {
            return CMSharedConstants.AFTER_VOTE_TIME;
        }

        return 10;
    }

    public String getCountDownText(int time) {
        String text = time + " " + (time == 1 ? "second" : "seconds") + " left";

        if (partyStatus.getState() == PartyStatus.State.BEFORE_VOTING_INTERMISSION) {
            text += " to start voting!";
        } else if (partyStatus.getState() == PartyStatus.State.VOTING) {
            text += " to vote!";
        } else if (partyStatus.getState() == PartyStatus.State.AFTER_VOTING_INTERMISSION) {
            text += " to start the minigame!";
        } else {
            text += "!";
        }

        return text;
    }

    public void disqualifyPlayer(ServerPlayer serverPlayer) {
        this.inGamePlayers.remove(serverPlayer);
        serverPlayer.kill();
    }

    public PartyStatus getPartyStatus() {
        return partyStatus;
    }

    public GenericMinigame getCurrentMinigame() {
        return currentMinigame;
    }

    @SuppressWarnings("unchecked")
    public <T extends GenericMinigame> T getCurrentMinigame(Class<T> minigameClass) {
        if (minigameClass.isInstance(currentMinigame)) {
            return (T) currentMinigame;
        }
        throw new ClassCastException("Current minigame is not of type " + minigameClass.getName());
    }

    public MapData getCurrentMapData() {
        return currentMapData;
    }

    public ServerLevel getServerLevel() {
        return serverLevel;
    }

    public List<ServerPlayer> getInGamePlayers() {
        return this.inGamePlayers;
    }
}
