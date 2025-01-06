package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.registries.server.ServerConfigRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.config.ConfigProvider;
import net.minecraft.server.MinecraftServer;

public class GameManager {
    private static GameManager instance;

    private final MinecraftServer server;

    private final ServerStatus serverStatus;
    private final PartyStatus partyStatus;

    private GameManager(MinecraftServer server) {
        this.server = server;

        this.serverStatus = new ServerStatus();
        this.partyStatus = new PartyStatus();
    }

    public static GameManager getInstance() {
        return GameManager.instance;
    }

    public static void onStart(MinecraftServer server) {
        GameManager newGameManager = new GameManager(server);
        GameManager.instance = newGameManager;

        ConfigProvider configProvider = ServerConfigRegistry.registerProvider(server);

        String configuredServerType = configProvider.get("server_type", String.class);
        newGameManager.getServerStatus().setType(ServerStatus.Type.valueOf(configuredServerType));
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public PartyStatus getPartyStatus() {
        return partyStatus;
    }
}