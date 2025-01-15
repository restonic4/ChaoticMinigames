package com.chaotic_loom.chaotic_minigames.entrypoints.constants;

import com.chaotic_loom.under_control.client.rendering.shader.ShaderProfile;
import com.chaotic_loom.under_control.registries.client.UnderControlShaders;
import net.minecraft.client.multiplayer.ServerData;

import java.util.ArrayList;
import java.util.List;

public class CMClientConstants {
    public static final List<ServerData> SERVERS = new ArrayList<>();

    public static final boolean RENDER_HITBOX = false;

    static {
        /*SERVERS.add(new ServerData(
                "Test server 1",
                "127.0.0.1:25565",
                false
        ));*/

        SERVERS.add(new ServerData(
                "Test server 1",
                "restonic4-tests.exaroton.me:40617",
                false
        ));
    }
}
