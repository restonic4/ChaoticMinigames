package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.PlayMusic;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.ThreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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

import java.util.List;

public class PartyManager {
    private final GameManager gameManager;
    private final PartyStatus partyStatus;
    private final ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);

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
        partyStatus.setState(PartyStatus.State.BEFORE_VOTING_INTERMISSION);

        PlayMusic.sendToAll(serverLevel.getServer(), SoundRegistry.MUSIC_MAIN_MENU_1, 2000, EasingSystem.EasingType.LINEAR);

        startCountDown();
    }

    private void onVote() {
        partyStatus.setState(PartyStatus.State.VOTING);

        startCountDown();
    }

    private void onAfterVote() {
        partyStatus.setState(PartyStatus.State.AFTER_VOTING_INTERMISSION);

        loadMap(serverLevel, "test");

        startCountDown();
    }

    private void onPlaying() {
        partyStatus.setState(PartyStatus.State.PLAYING);

        PlayMusic.sendToAll(serverLevel.getServer(), SoundEvents.MUSIC_DISC_PIGSTEP, 2000, EasingSystem.EasingType.LINEAR);

        startCountDown();
    }

    private void onAfterPlaying() {
        unLoadMap(serverLevel);
    }

    private void loadMap(ServerLevel serverLevel, String structureName) {
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
        ThreadUtils.runCountDown(getCountDownTime(partyStatus.getState()), (timeLeft) -> {
            gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
        });
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

    public PartyStatus getPartyStatus() {
        return partyStatus;
    }
}
