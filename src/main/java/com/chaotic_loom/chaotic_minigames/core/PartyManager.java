package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.util.ThreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class PartyManager {
    private final GameManager gameManager;
    private final PartyStatus partyStatus;

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
        ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);

        while (true) {
            partyStatus.setState(PartyStatus.State.BEFORE_VOTING_INTERMISSION);

            ThreadUtils.runCountDown(CMSharedConstants.BEFORE_VOTE_TIME, (timeLeft) -> {
                gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
            });

            partyStatus.setState(PartyStatus.State.VOTING);

            ThreadUtils.runCountDown(CMSharedConstants.VOTE_TIME, (timeLeft) -> {
                gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
            });

            partyStatus.setState(PartyStatus.State.AFTER_VOTING_INTERMISSION);

            loadMap(serverLevel, "test");

            ThreadUtils.runCountDown(CMSharedConstants.AFTER_VOTE_TIME, (timeLeft) -> {
                gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
            });

            partyStatus.setState(PartyStatus.State.PLAYING);

            ThreadUtils.runCountDown(CMSharedConstants.AFTER_VOTE_TIME, (timeLeft) -> {
                gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
            });

            unLoadMap(serverLevel);
        }
    }

    private void loadMap(ServerLevel serverLevel, String structureName) {
        serverLevel.getServer().execute(() -> {
            StructureTemplateManager structureManager = serverLevel.getStructureManager();

            BlockPos position = new BlockPos(0, 0, 0);

            ResourceLocation structureId = new ResourceLocation(CMSharedConstants.ID, structureName);
            StructureTemplate structure = structureManager.get(structureId).orElse(null);

            if (structure == null) {
                System.err.println("Could not place the structure: " + structureName);
                return;
            }

            StructurePlaceSettings placementData = new StructurePlaceSettings()
                    .setMirror(null)
                    .setRotation(null)
                    .setIgnoreEntities(false);

            structure.placeInWorld(serverLevel, position, position, placementData, serverLevel.getRandom(), 2);

            System.out.println("Structure " + structureName + " placed on " + position);
        });
    }

    private void unLoadMap(ServerLevel serverLevel) {
        serverLevel.getServer().execute(() -> {
            for (int x = 0; x <= 160; x++) {
                for (int y = 0; y <= 160; y++) {
                    for (int z = 0; z <= 160; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        });
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
