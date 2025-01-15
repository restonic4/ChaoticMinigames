package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CMSharedConstants {
    public static final String ID = "chaotic_minigames";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final int STRUCTURE_BLOCK_MAX_SIZE = 160;

    public static final int BEFORE_VOTE_TIME = 5;
    public static final int VOTE_TIME = 10;
    public static final int AFTER_VOTE_TIME = 10;

    public static final float[] netherTopColor = {0.9f, 0.2f, 0.1f, 1.0f};
    public static final float[] netherBottomColor = {0.3f, 0.05f, 0.02f, 1.0f};

    public static final float[] endTopColor = {0.4f, 0.4f, 0.5f, 1.0f};
    public static final float[] endBottomColor = {0.1f, 0.05f, 0.2f, 1.0f};

    public static final List<BlockPos> LOBBY_SPAWNS = new ArrayList<>();

    static {
        LOBBY_SPAWNS.add(new BlockPos(15000, 70, 15000));
    }
}
