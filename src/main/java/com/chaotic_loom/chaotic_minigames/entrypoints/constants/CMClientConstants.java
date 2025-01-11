package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import net.minecraft.client.multiplayer.ServerData;

import java.util.ArrayList;
import java.util.List;

public class CMClientConstants {
    public static final List<ServerData> SERVERS = new ArrayList<>();

    static {
        SERVERS.add(new ServerData(
                "Test server 1",
                "127.0.0.1:25565",
                false
        ));
    }
}
