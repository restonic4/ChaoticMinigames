package com.chaotic_loom.chaotic_minigames.core;

import com.chaotic_loom.chaotic_minigames.core.data.*;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.core.registries.common.SoundRegistry;
import com.chaotic_loom.chaotic_minigames.core.registries.common.MinigameRegistry;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.networking.packets.server_to_client.*;
import com.chaotic_loom.under_control.events.EventResult;
import com.chaotic_loom.under_control.events.types.LivingEntityExtraEvents;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.ThreadHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants.LOBBY_SPAWNS;

public class PartyManager {
    public static final int MIN_HEIGHT = -64;
    public static final int AREA_RANGE = 160;

    private final Playlist music;

    private final GameManager gameManager;
    private final PartyStatus partyStatus;
    private final ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);
    private GenericMinigame currentMinigame;
    private MapData currentMapData;
    private List<ServerPlayer> inGamePlayers = new ArrayList<>();
    private Map<ServerPlayer, String> votes = new HashMap<>();
    private GenericMinigame[] votingMinigames;
    private Map<ServerPlayer, Vector3f> frozenPlayers = new HashMap<>();

    private boolean hasMapBeenLoaded = false;
    private boolean shouldRestart = false;
    private boolean allowDamage = false;

    public PartyManager() {
        this.gameManager = GameManager.getInstance();
        this.partyStatus = new PartyStatus();
        this.partyStatus.setState(PartyStatus.State.IDLE);

        this.music = new Playlist();

        this.music.addMusic(SoundRegistry.MUSIC_MAIN_MENU_1);
        this.music.addMusic(SoundRegistry.ROLLERDISCO_RUMBLE);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_spooky_thoughts, 41f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_lighthouse, 33f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_coconut_mystery, 47.5f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_sir_ghostington, 60f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_melancholy, 48f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_the_adventure, 62f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_red_lights, 42f);
        this.music.addMusic(SoundRegistry.ghost_and_ghost_lazy_sunday, 47f);
        this.music.addMusic(SoundRegistry.sybranax_rogue, 37f);
        this.music.addMusic(SoundRegistry.meizong_rebirth, 43f);
        this.music.addMusic(SoundRegistry.dugn1r_45_degrees, 28.5f);
        this.music.addMusic(SoundRegistry.redvox_time, 140.5f);
        this.music.addMusic(SoundRegistry.dugn1r_monster_inside_you, 31.5f);
        this.music.addMusic(SoundRegistry.redvox_the_ruby, 67f);
        this.music.addMusic(SoundRegistry.aron_kruk_astral_finale, 136f);
        this.music.addMusic(SoundRegistry.aaron_kruk_orion_s_reverie, 47f);
        this.music.addMusic(SoundRegistry.dino_rano_not_the_one, 29.75f);
        this.music.addMusic(SoundRegistry.aaron_kruk_zenith, 52.75f);
    }

    public void onStart() {
        ServerLevel serverLevel = GameManager.getInstance().getServer().getLevel(Level.OVERWORLD);

        resetServerLevel();

        ServerPlayConnectionEvents.DISCONNECT.register((serverGamePacketListener, minecraftServer) -> {
            ServerPlayer serverPlayer = serverGamePacketListener.getPlayer();
            disqualifyPlayer(serverPlayer);
        });

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            for (Map.Entry<ServerPlayer, Vector3f> entry : frozenPlayers.entrySet()) {
                ServerPlayer serverPlayer = entry.getKey();
                Vector3f frozenPosition = entry.getValue();

                serverPlayer.setDeltaMovement(0, 0, 0);

                if (frozenPosition.distance((float) serverPlayer.position().x, (float) serverPlayer.position().y, (float) serverPlayer.position().z) >= 1.5f) {
                    serverPlayer.moveTo(frozenPosition.x(), frozenPosition.y(), frozenPosition.z());
                }
            }
        });

        LivingEntityExtraEvents.PUSHABLE.register((entity, actor) -> {
            if (entity instanceof Player) {
                return EventResult.CANCELED;
            }

            return EventResult.CONTINUE;
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((livingEntity, damageSource, amount) -> {
            if (allowDamage) {
                return true;
            }

            return !(livingEntity instanceof Player) || damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.OUTSIDE_BORDER) || damageSource.is(DamageTypes.GENERIC) || damageSource.is(DamageTypes.GENERIC_KILL);
        });

        loop();
    }

    public void resetServerLevel() {
        serverLevel.setDefaultSpawnPos(new BlockPos(0, 0,0), 10);

        serverLevel.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(0, serverLevel.getServer());
        serverLevel.getServer().setDifficulty(Difficulty.HARD, true);

        serverLevel.setDayTime(1000);
        serverLevel.setWeatherParameters(0, 0, false, false);
    }

    public void loop() {
        shouldRestart = false;

        onAfterPlaying(); // Unload weird things if the server crashed

        int timePassedWithoutPlayers = 0;
        while (serverLevel.getServer().getPlayerList().getPlayerCount() <= 0) {
            ThreadHelper.sleep(1000);
            timePassedWithoutPlayers++;
        }

        CMSharedConstants.LOGGER.info("Time passed without players: " + MathHelper.formatTime(timePassedWithoutPlayers));

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

        setState(PartyStatus.State.AFTER_VOTING_INTERMISSION);

        loadMap(serverLevel, currentMapData.getStructureId());

        startCountDown();

        int secondsWaited = 0;
        while (!hasMapBeenLoaded) {
            gameManager.sendSubtitleToPlayers(Component.literal("Waiting for map... (" + secondsWaited + ")"));
            ThreadHelper.sleep(1000);
            secondsWaited++;
        }

        unFreezeAll();
    }

    private void onPlaying() {
        System.out.println("Playing");

        ClientMinigameStart.sendToAll(serverLevel.getServer(), currentMinigame.getSettings().getId());

        if (serverLevel.getServer().getPlayerCount() < currentMinigame.getSettings().getMinPlayers() || serverLevel.getServer().getPlayerCount() > currentMinigame.getSettings().getMaxPlayers()) {
            shouldRestart = true;

            gameManager.sendTitleToPlayers(Component.translatable("message.chaotic_minigames.not_enough_players"), 40, 100, 40);

            ThreadHelper.sleep(10000);

            return;
        }

        setState(PartyStatus.State.PLAYING);

        resetInventories();
        inGamePlayers.addAll(serverLevel.getServer().getPlayerList().getPlayers());
        currentMinigame.onServerStart(this);
    }

    private void onAfterPlaying() {
        System.out.println("After playing");

        if (currentMinigame != null) {
            currentMinigame.stopTickingOnServer(); // Just in case
            currentMinigame.serverCleanup();
            ClientMinigameCleanup.sendToAll(serverLevel.getServer(), currentMinigame.getSettings().getId());
        }

        teleportLobby();
        resetInventories();
        unFreezeAll();
        disallowDamage();

        unLoadMap(serverLevel);
        currentMapData = null;
        currentMinigame = null;
        inGamePlayers.clear();

        resetServerLevel();
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
        if (currentMapData instanceof SpawnerMapData spawnerMapData) {
            for (ServerPlayer serverPlayer : inGamePlayers) {
                BlockPos spawnPos = spawnerMapData.getSpawns().getRandom().getBlockPos();
                serverPlayer.teleportTo(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);
            }
        }
    }

    public void teleportRandomly(MapList<MapSpawn> spawns, List<ServerPlayer> players) {
        for (ServerPlayer serverPlayer : players) {
            BlockPos spawnPos = spawns.getRandom().getBlockPos();
            serverPlayer.teleportTo(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);
        }
    }

    public void teleportRandomly(MapList<MapSpawn> spawns, ServerPlayer... players) {
        for (ServerPlayer serverPlayer : players) {
            BlockPos spawnPos = spawns.getRandom().getBlockPos();
            serverPlayer.teleportTo(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);
        }
    }

    public void teleportInOrder() {
        int spawnIndex = 0;

        if (currentMapData instanceof SpawnerMapData spawnerMapData) {
            for (ServerPlayer serverPlayer : inGamePlayers) {
                BlockPos spawnPos = spawnerMapData.getSpawns().get(spawnIndex).getBlockPos();
                serverPlayer.teleportTo(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);

                if (spawnIndex + 1 >= spawnerMapData.getSpawns().size()) {
                    spawnIndex = 0;
                } else {
                    spawnIndex++;
                }
            }
        }
    }

    public void teleportLobby() {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            teleportLobby(serverPlayer);
        }
    }

    public void teleportLobby(ServerPlayer serverPlayer) {
        BlockPos spawnPos = getRandomLobbySpawn();
        serverPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
    }

    public void resetInventories() {
        for (ServerPlayer serverPlayer : inGamePlayers) {
            resetInventory(serverPlayer);
        }
    }

    public void resetInventory(ServerPlayer serverPlayer) {
        serverPlayer.getInventory().clearContent();
        serverPlayer.removeAllEffects();
        serverPlayer.setHealth(serverPlayer.getMaxHealth());
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
                shouldRestart = true;
                hasMapBeenLoaded = true;
                return;
            }

            StructurePlaceSettings placementData = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false);

            structure.placeInWorld(serverLevel, position, position, placementData, serverLevel.getRandom(), 2);

            GameManager.LOGGER.info("Structure {} placed on {}", structureName, position);

            if (getCurrentMapData().getOnLoad() != null) {
                getCurrentMapData().getOnLoad().run();
            }

            hasMapBeenLoaded = true;
        });
    }

    private void unLoadMap(ServerLevel serverLevel) {
        if (getCurrentMapData() != null && getCurrentMapData().getOnUnLoad() != null) {
            getCurrentMapData().getOnUnLoad().run();
        }

        serverLevel.getServer().execute(() -> {
            for (int x = -1; x <= AREA_RANGE + 1; x++) {
                for (int y = MIN_HEIGHT; y <= AREA_RANGE + 1; y++) {
                    for (int z = -1; z <= AREA_RANGE + 1; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }

            AABB area = new AABB(0, MIN_HEIGHT, 0, AREA_RANGE, AREA_RANGE, AREA_RANGE);
            List<Entity> entities = serverLevel.getEntities(null, area);

            for (Entity entity : entities) {
                if (!(entity instanceof Player)) {
                    entity.discard();
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
        ThreadHelper.runCountDown(getCountDownTime(partyStatus.getState()), null, (timeLeft) -> {
            gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
            return true;
        });
    }

    public void startCountDown(int seconds, Runnable runnable) {
        ThreadHelper.runCountDown(seconds, runnable, (timeLeft) -> {
            gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));
            return !inGamePlayers.isEmpty();
        });
    }

    public void startCountDown(int seconds, Runnable runnable, Predicate<Integer> cancelCondition) {
        ThreadHelper.runCountDown(seconds, runnable, (timeLeft) -> {
            gameManager.sendSubtitleToPlayers(Component.literal(getCountDownText(timeLeft)));

            return !cancelCondition.test(timeLeft);
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

    public ServerPlayer getRandomInGamePlayer() {
        if (inGamePlayers == null || inGamePlayers.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(inGamePlayers.size());
        return inGamePlayers.get(randomIndex);
    }

    public void disqualifyPlayer(ServerPlayer serverPlayer) {
        disqualifyPlayer(serverPlayer, true);
    }

    public void disqualifyPlayer(ServerPlayer serverPlayer, boolean reset) {
        this.inGamePlayers.remove(serverPlayer);

        if (reset) {
            serverPlayer.kill();
        }

        CMSharedConstants.LOGGER.info("{} disqualified!", serverPlayer.getDisplayName());
    }

    public void freezePlayer(ServerPlayer player, Vector3f position) {
        CMSharedConstants.LOGGER.info("Freezing {}", player.getDisplayName());

        frozenPlayers.put(player, position);
        FreezePlayer.sendToClient(player, position);
    }

    public void unFreezePlayer(ServerPlayer player) {
        CMSharedConstants.LOGGER.info("UnFreezing {}", player.getDisplayName());

        frozenPlayers.remove(player);
        FreezePlayer.sendToClient(player, null);
    }

    public void freezeAll() {
        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            freezePlayer(serverPlayer, serverPlayer.position().toVector3f());
        }
    }

    public void unFreezeAll() {
        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            unFreezePlayer(serverPlayer);
        }
    }

    public void unFreezeAll(ServerPlayer... playerExceptions) {
        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            boolean shouldPlayerBeSkipped = false;

            for (int i = 0; i < playerExceptions.length && !shouldPlayerBeSkipped; i++) {
                ServerPlayer exception = playerExceptions[i];

                if (exception.equals(serverPlayer)) {
                    shouldPlayerBeSkipped = true;
                }
            }

            if (!shouldPlayerBeSkipped) {
                unFreezePlayer(serverPlayer);
            }
        }
    }

    public Vector3f getFrozenPositionIfContained(ServerPlayer player) {
        return frozenPlayers.get(player);
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

    public void allowDamage() {
        this.allowDamage = true;
    }

    public void disallowDamage() {
        this.allowDamage = false;
    }
}
