package com.chaotic_loom.chaotic_minigames.core.registries.server;

import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.config.ConfigProvider;
import net.minecraft.server.MinecraftServer;

public class ServerConfigRegistry {
    public static ConfigProvider registerProvider(MinecraftServer server) {
        ConfigProvider configProvider = ConfigAPI.registerServerConfig(CMSharedConstants.ID, server);

        configProvider.registerOption("server_type", ServerStatus.Type.LOBBY.name(), "The server type");

        return configProvider;
    }
}
