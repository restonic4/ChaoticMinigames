package com.chaotic_loom.chaotic_minigames.core.data;

public class SpawnerMapData extends MapData {
    private final MapList<MapSpawn> spawns;

    public SpawnerMapData(String structureId, MapList<MapSpawn> spawns) {
        super(structureId);
        this.spawns = spawns;
    }

    public MapList<MapSpawn> getSpawns() {
        return spawns;
    }
}
