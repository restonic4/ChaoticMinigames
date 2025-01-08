package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CMSharedConstants {
    public static final String ID = "chaotic_minigames";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final List<ServerData> SERVERS = new ArrayList<>();

    public static final int STRUCTURE_BLOCK_MAX_SIZE = 160;

    public static final int BEFORE_VOTE_TIME = 10;
    public static final int VOTE_TIME = 10;
    public static final int AFTER_VOTE_TIME = 10;

    static {
        SERVERS.add(new ServerData(
                "Test server 1",
                "restonic4-tests.exaroton.me:40617",
                false
        ));
    }
}
