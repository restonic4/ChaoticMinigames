package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.server.ServerAPI;
import com.chaotic_loom.under_control.util.JavaHelper;
import com.chaotic_loom.under_control.util.data_holders.ServerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

import java.util.concurrent.atomic.AtomicReference;

public class ServerNetworkManager {
    public static void matchServer() {
        AtomicReference<ServerData> serverData = new AtomicReference<>();

        JavaHelper.processRandomly(CMClientConstants.SERVERS, (foundServerData) -> {
            ServerInfo response = ServerAPI.getServerData(foundServerData.ip);

            if (response == null) {
                return;
            }

            ServerInfo.Players players = response.getPlayers();

            if (players != null && players.getOnline() < players.getMax()) {
                serverData.set(new ServerData(foundServerData.name, foundServerData.ip, foundServerData.isLan()));
            }
        });

        if (serverData.get() != null) {
            ServerAddress serverAddress = ServerAddress.parseString(serverData.get().ip);
            ConnectScreen.startConnecting(new ServerListScreen(new TitleScreen()), Minecraft.getInstance(), serverAddress, serverData.get(), true);
        }
    }
}
