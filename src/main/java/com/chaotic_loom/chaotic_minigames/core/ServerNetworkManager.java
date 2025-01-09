package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.server.ServerAPI;
import com.chaotic_loom.under_control.util.JavaHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

import java.util.concurrent.atomic.AtomicReference;

public class ServerNetworkManager {
    public static void matchServer() {
        AtomicReference<ServerData> serverData = new AtomicReference<>();

        JavaHelper.processRandomly(CMSharedConstants.SERVERS, (foundServerData) -> {
            com.chaotic_loom.under_control.util.ServerData response = ServerAPI.getServerData(foundServerData.ip);

            if (response.getPlayers().getOnline() < response.getPlayers().getMax()) {
                serverData.set(new ServerData(foundServerData.name, foundServerData.ip, foundServerData.isLan()));
            }
        });

        ServerAddress serverAddress = ServerAddress.parseString(serverData.get().ip);
        ConnectScreen.startConnecting(new ServerListScreen(new TitleScreen()), Minecraft.getInstance(), serverAddress, serverData.get(), true);
    }
}
