package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.ServerStatus;
import com.chaotic_loom.chaotic_minigames.core.registries.server.ServerConfigRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.SendServerDataToClient;
import com.chaotic_loom.under_control.api.config.ConfigAPI;
import com.chaotic_loom.under_control.config.ConfigProvider;
import com.chaotic_loom.under_control.util.SynchronizationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

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
        throw  new RuntimeException("[Thread-9/ERROR]: Uncaught exception in thread \"Thread-9\"\n" +
                "java.util.ConcurrentModificationException: null\n" +
                "at java.util.HashMap$HashIterator.nextNode(HashMap.java:1597) ~[?:?]\n" +
                "at java.util.HashMap$EntryIterator.next(HashMap.java:1630) ~[?:?]\n" +
                "at java.util.HashMap$EntryIterator.next(HashMap.java:1628) ~[?:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.PartyManager.unFreezeAll(PartyManager.java:523) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.Sweeper.onStart(Sweeper.java:118) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.PartyManager.onPlaying(PartyManager.java:220) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.PartyManager.loop(PartyManager.java:117) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.PartyManager.loop(PartyManager.java:127) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.PartyManager.onStart(PartyManager.java:95) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at com.chaotic_loom.chaotic_minigames.core.GameManager.lambda$onStart$0(GameManager.java:57) ~[ChaoticMinigames-1.0.jar:?]\n" +
                "at java.lang.Thread.run(Thread.java:840) ~[?:?]");

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

    public void sendTitleToPlayers(Component component, int fadeIn, int stay, int fadeOut) {
        ClientboundSetTitleTextPacket titleTextPacket = new ClientboundSetTitleTextPacket(component);
        ClientboundSetTitlesAnimationPacket titleAnimationPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);

        for (ServerPlayer serverPlayer : GameManager.getInstance().getServer().getPlayerList().getPlayers()) {
            serverPlayer.connection.send(titleTextPacket);
            serverPlayer.connection.send(titleAnimationPacket);
        }
    }

    public void spawnPlayer(ServerPlayer serverPlayer, boolean isNew) {
        if (isNew) {
            SendServerDataToClient.sendToClient(serverPlayer);
        }

        BlockPos randomSpawn = getRandomLobbySpawn();

        serverPlayer.moveTo(randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ());
        serverPlayer.setGameMode(GameType.SURVIVAL);
        serverPlayer.getInventory().clearContent();
    }

    private static final Random RANDOM = new Random();

    private static BlockPos getRandomLobbySpawn() {
        if (CMSharedConstants.LOBBY_SPAWNS.isEmpty()) {
            return null;
        }

        return CMSharedConstants.LOBBY_SPAWNS.get(RANDOM.nextInt(CMSharedConstants.LOBBY_SPAWNS.size()));
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
