package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.server.ServerAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ServerManager {
    public static void matchServer() {
        AtomicReference<ServerData> serverData = new AtomicReference<>();

        processRandomly(CMSharedConstants.SERVERS, (foundServerData) -> {
            com.chaotic_loom.under_control.util.ServerData response = ServerAPI.getServerData(foundServerData.ip);

            if (response.getPlayers().getOnline() < response.getPlayers().getMax()) {
                serverData.set(new ServerData(foundServerData.name, foundServerData.ip, foundServerData.isLan()));
            }
        });

        ServerAddress serverAddress = ServerAddress.parseString(serverData.get().ip);
        ConnectScreen.startConnecting(new ServerListScreen(new TitleScreen()), Minecraft.getInstance(), serverAddress, serverData.get(), true);
    }

    @Deprecated(forRemoval = true)
    public static <T> void processRandomly(List<T> list, Consumer<T> action) {
        List<T> copy = new ArrayList<>(list);

        Collections.shuffle(copy);

        for (T element : copy) {
            action.accept(element);
        }
    }
}
