package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CMSharedConstants {
    public static final String ID = "chaotic_minigames";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String SERVER_IP = "restonic4-tests.exaroton.me"; // minigames.chaotic-loom.com
    public static final int SERVER_PORT = 40617;
    public static final String SERVER_FULL_IP = SERVER_IP + ":" + SERVER_PORT;
}
