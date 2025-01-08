package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.registries.server.ServerConfigRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.config.ConfigProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameManager {
    public static final Logger LOGGER = LogManager.getLogger();

    private static GameManager instance;
    private static PartyManager partyManager;

    private final MinecraftServer server;

    private final ServerStatus serverStatus;


    private GameManager(MinecraftServer server) {
        this.server = server;

        this.serverStatus = new ServerStatus();
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

        if (newGameManager.getServerStatus().getType() == ServerStatus.Type.PARTY) {
            partyManager = new PartyManager();

            new Thread(() -> partyManager.onStart()).start();
        }
    }

    public void sendSubtitleToPlayers(Component component) {
        ClientboundSetActionBarTextPacket actionBarPacket = new ClientboundSetActionBarTextPacket(component);

        for (ServerPlayer serverPlayer : GameManager.getInstance().getServer().getPlayerList().getPlayers()) {
            serverPlayer.connection.send(actionBarPacket);
        }
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}
