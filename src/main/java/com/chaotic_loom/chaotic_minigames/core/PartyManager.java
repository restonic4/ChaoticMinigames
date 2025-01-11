package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.MinigameSettings;
import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.data.Playlist;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMClientConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.PlayMusic;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.SendServerDataToClient;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.SendVoteDataToClient;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.SyncClients;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Consumer;

import static com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants.LOBBY_SPAWNS;

public class PartyManager {
    private final Playlist music;

    private final GameManager gameManager;
    private final PartyStatus partyStatus;
    private final ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);
    private GenericMinigame currentMinigame;
    private MapData currentMapData;
    private List<ServerPlayer> inGamePlayers = new ArrayList<>();
    private Map<ServerPlayer, String> votes = new HashMap<>();
    private GenericMinigame[] votingMinigames;

    private boolean hasMapBeenLoaded = false;
    private boolean shouldRestart = false;

    public PartyManager() {
        this.gameManager = GameManager.getInstance();
        this.partyStatus = new PartyStatus();

        this.music = new Playlist();

        this.music.addMusic(SoundRegistry.MUSIC_MAIN_MENU_1);
    }

    public void onStart() {
        ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);
        serverLevel.setDefaultSpawnPos(new BlockPos(0, 0,0), 10);

        serverLevel.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, serverLevel.getServer());
        serverLevel.getServer().setDifficulty(Difficulty.HARD, true);

        serverLevel.setDayTime(1000);
        serverLevel.setWeatherParameters(0, 0, false, false);

        loop();
    }

    public void loop() {
        shouldRestart = false;

        onAfterPlaying(); // Unload weird things if the server crashed

        while (!shouldRestart) {
            if (!shouldRestart) {
                onBeforeVote();
            }

            if (!shouldRestart) {
                onVote();
            }

            if (!shouldRestart) {
                onAfterVote();
            }

            if (!shouldRestart) {
                onPlaying();
            }

            if (!shouldRestart) {
                onAfterPlaying();
            }
        }

        CMSharedConstants.LOGGER.warn("RESTARTING PARTY");

        loop();
    }

    private void onBeforeVote() {
        System.out.println("Before vote");

        setState(PartyStatus.State.BEFORE_VOTING_INTERMISSION);

        music.playRandom();

        SyncClients.sendToAll(serverLevel.getServer());

        ThreadHelper.sleep(5000);

        startCountDown();
    }

    private void onVote() {
        System.out.println("Vote");

        gameManager.sendTitleToPlayers(Component.translatable("message.chaotic_minigames.how_to_vote"), 40, 100, 40);

        votingMinigames = selectMinigamesForVoting(serverLevel.getServer().getPlayerCount());

        if (votingMinigames == null) {
            gameManager.sendTitleToPlayers(Component.translatable("message.chaotic_minigames.not_enough_players"), 40, 100, 40);

            ThreadHelper.sleep(10000);

            shouldRestart = true;

            return;
        }

        SendVoteDataToClient.sendToAll(serverLevel.getServer(), votingMinigames[0].getSettings().getId(), votingMinigames[1].getSettings().getId(), votingMinigames[2].getSettings().getId());

        setState(PartyStatus.State.VOTING);

        startCountDown();
    }

    private void onAfterVote() {
        System.out.println("After vote");

        setState(PartyStatus.State.AFTER_VOTING_INTERMISSION);

        String minigameId = getWinningVote();
        GenericMinigame minigame = null;

        CMSharedConstants.LOGGER.info("Chosen minigame: {}", minigameId);

        for (int i = 0; i < MinigameRegistry.MINIGAMES.size() && minigame == null; i++) {
            GenericMinigame genericMinigame = MinigameRegistry.MINIGAMES.get(i);

            if (genericMinigame.getSettings().getId().equals(minigameId)) {
                minigame = genericMinigame;
            }
        }

        votingMinigames = null;
        votes.clear();

        currentMinigame = minigame;
        currentMapData = currentMinigame.getSettings().getMaps().getRandom();

        loadMap(serverLevel, currentMapData.getStrucutreId());

        startCountDown();

        int secondsWaited = 0;
        while (!hasMapBeenLoaded) {
            gameManager.sendSubtitleToPlayers(Component.literal("Waiting for map... (" + secondsWaited + ")"));
            ThreadHelper.sleep(1000);
            secondsWaited++;
        }
    }

    private void onPlaying() {
        System.out.println("Playing");

        if (serverLevel.getServer().getPlayerCount() < currentMinigame.getSettings().getMinPlayers() || serverLevel.getServer().getPlayerCount() > currentMinigame.getSettings().getMaxPlayers()) {
            shouldRestart = true;

            gameManager.sendTitleToPlayers(Component.translatable("message.chaotic_minigames.not_enough_players"), 40, 100, 40);

            ThreadHelper.sleep(10000);

            return;
        }

        setState(PartyStatus.State.PLAYING);

        inGamePlayers.addAll(serverLevel.getServer().getPlayerList().getPlayers());
        currentMinigame.onStart(this);
    }

    private void onAfterPlaying() {
        System.out.println("After playing");

        if (currentMinigame != null) {
            currentMinigame.stopTickingOnServer(); // Just in case
        }

        teleportLobby();

        unLoadMap(serverLevel);
        currentMapData = null;
        currentMinigame = null;
        inGamePlayers.clear();
    }

    private void setState(PartyStatus.State state) {
        partyStatus.setState(state);

        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            SendServerDataToClient.sendToClient(serverPlayer);
        }
    }

    public void voteReceived(ServerPlayer serverPlayer, String minigameId) {
        votes.put(serverPlayer, minigameId);
    }

    public String getWinningVote() {
        Map<String, Integer> validVotes = new HashMap<>();

        for (String vote : votes.values()) {
            if (isValidVote(vote)) {
                validVotes.put(vote, validVotes.getOrDefault(vote, 0) + 1);
            }
        }

        if (validVotes.isEmpty()) {
            return getRandomValidVote();
        }

        int maxVotes = Collections.max(validVotes.values());

        List<String> topVotes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : validVotes.entrySet()) {
            if (entry.getValue() == maxVotes) {
                topVotes.add(entry.getKey());
            }
        }

        if (topVotes.size() > 1) {
            Random random = new Random();
            return topVotes.get(random.nextInt(topVotes.size()));
        } else {
            return topVotes.get(0);
        }
    }

    private boolean isValidVote(String vote) {
        for (GenericMinigame minigame : votingMinigames) {
            if (minigame.getSettings().getId().equals(vote)) {
                return true;
            }
        }
        return false;
    }

    private String getRandomValidVote() {
        List<String> validMinigames = new ArrayList<>();
        for (GenericMinigame minigame : votingMinigames) {
            validMinigames.add(minigame.getSettings().getId());
        }

        if (validMinigames.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return validMinigames.get(random.nextInt(validMinigames.size()));
    }

    private static final Random RANDOM = new Random();

    public static BlockPos getRandomLobbySpawn() {
        if (LOBBY_SPAWNS.isEmpty()) {
            return null;
        }

        return LOBBY_SPAWNS.get(RANDOM.nextInt(LOBBY_SPAWNS.size()));
    }

    public void teleportRandomly() {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            BlockPos spawnPos = currentMapData.getSpawns().getRandom().getBlockPos();
            serverPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        }
    }

    public void teleportLobby() {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            BlockPos spawnPos = getRandomLobbySpawn();
            serverPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        }
    }

    public void loadMapWeather() {
        if (serverLevel == null) {
            return;
        }

        serverLevel.setDayTime(getCurrentMapData().getTime());
        serverLevel.setWeatherParameters(0, 0, getCurrentMapData().isRain(), false);
    }

    private void loadMap(ServerLevel serverLevel, String structureName) {
        hasMapBeenLoaded = false;

        serverLevel.getServer().execute(() -> {
            StructureTemplateManager structureManager = serverLevel.getStructureManager();

            BlockPos position = new BlockPos(0, 0, 0);

            ResourceLocation structureId = new ResourceLocation(CMSharedConstants.ID, structureName);
            StructureTemplate structure = structureManager.get(structureId).orElse(null);

            if (structure == null) {
                GameManager.LOGGER.error("Could not place the structure: {}", structureName);
                return;
            }

            StructurePlaceSettings placementData = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false);

            structure.placeInWorld(serverLevel, position, position, placementData, serverLevel.getRandom(), 2);

            GameManager.LOGGER.info("Structure {} placed on {}", structureName, position);

            hasMapBeenLoaded = true;
        });
    }

    private void unLoadMap(ServerLevel serverLevel) {
        serverLevel.getServer().execute(() -> {
            AABB area = new AABB(0, 0, 0, 160, 160, 160);
            List<Entity> entities = serverLevel.getEntities(null, area);

            for (Entity entity : entities) {
                if (!(entity instanceof Player)) {
                    entity.discard();
                }
            }

            for (int x = 0; x <= 160; x++) {
                for (int y = 0; y <= 160; y++) {
                    for (int z = 0; z <= 160; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        });

        GameManager.LOGGER.info("Map unloaded");
    }

    public static GenericMinigame[] selectMinigamesForVoting(int playerCount) {
        List<GenericMinigame> allMinigames = MinigameRegistry.MINIGAMES;

        List<GenericMinigame> eligibleMinigames = new ArrayList<>();
        for (GenericMinigame minigame : allMinigames) {
            MinigameSettings settings = minigame.getSettings();
            if (playerCount >= settings.getMinPlayers() && playerCount <= settings.getMaxPlayers()) {
                eligibleMinigames.add(minigame);
            }
        }

        if (eligibleMinigames.isEmpty()) {
            CMSharedConstants.LOGGER.error("Minigames not found for this amount of players: {}", playerCount);
            return null;
        }

        GenericMinigame[] selectedMinigames = new GenericMinigame[3];
        int count = Math.min(3, eligibleMinigames.size());

        Collections.shuffle(eligibleMinigames, RANDOM);

        for (int i = 0; i < count; i++) {
            selectedMinigames[i] = eligibleMinigames.get(i);
        }

        for (int i = count; i < 3; i++) {
            selectedMinigames[i] = eligibleMinigames.get(RANDOM.nextInt(eligibleMinigames.size()));
        }

        return selectedMinigames;
    }

    public void startCountDown() {
        startCountDown(getCountDownTime(partyStatus.getState()), null);
    }

    public void startCountDown(int seconds, Runnable runnable) {
        ThreadHelper.runCountDown(seconds, runnable, (timeLeft) -> {
            gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
        });
    }

    public void executeForAllInGame(Consumer<ServerPlayer> consumer) {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            consumer.accept(serverPlayer);
        }
    }

    public int getCountDownTime(PartyStatus.State state) {
        if (state == PartyStatus.State.BEFORE_VOTING_INTERMISSION) {
            return CMSharedConstants.BEFORE_VOTE_TIME;
        } else if (state == PartyStatus.State.VOTING) {
            return CMSharedConstants.VOTE_TIME;
        } else if (state == PartyStatus.State.AFTER_VOTING_INTERMISSION) {
            return CMSharedConstants.AFTER_VOTE_TIME;
        }

        return 10;
    }

    public String getCountDownText(int time) {
        String text = time + " " + (time == 1 ? "second" : "seconds") + " left";

        if (partyStatus.getState() == PartyStatus.State.BEFORE_VOTING_INTERMISSION) {
            text += " to start voting!";
        } else if (partyStatus.getState() == PartyStatus.State.VOTING) {
            text += " to vote!";
        } else if (partyStatus.getState() == PartyStatus.State.AFTER_VOTING_INTERMISSION) {
            text += " to start the minigame!";
        } else {
            text += "!";
        }

        return text;
    }

    public void disqualifyPlayer(ServerPlayer serverPlayer) {
        this.inGamePlayers.remove(serverPlayer);
        serverPlayer.kill();
    }

    public PartyStatus getPartyStatus() {
        return partyStatus;
    }

    public GenericMinigame getCurrentMinigame() {
        return currentMinigame;
    }

    @SuppressWarnings("unchecked")
    public <T extends GenericMinigame> T getCurrentMinigame(Class<T> minigameClass) {
        if (minigameClass.isInstance(currentMinigame)) {
            return (T) currentMinigame;
        }
        throw new ClassCastException("Current minigame is not of type " + minigameClass.getName());
    }

    public MapData getCurrentMapData() {
        return currentMapData;
    }

    @SuppressWarnings("unchecked")
    public <T extends MapData> T getCurrentMapData(Class<T> mapClass) {
        if (mapClass.isInstance(currentMapData)) {
            return (T) currentMapData;
        }
        throw new ClassCastException("Current map is not of type " + mapClass.getName());
    }

    public ServerLevel getServerLevel() {
        return serverLevel;
    }

    public List<ServerPlayer> getInGamePlayers() {
        return this.inGamePlayers;
    }
}
