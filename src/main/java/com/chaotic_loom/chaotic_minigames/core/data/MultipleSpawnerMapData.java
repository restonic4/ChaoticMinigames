package com.chaotic_loom.chaotic_minigames.core.data;

import java.util.Arrays;
import java.util.List;

public class MultipleSpawnerMapData extends MapData {
    private final List<MapList<MapSpawn>> spawns;

    @SafeVarargs
    public MultipleSpawnerMapData(String structureId, MapList<MapSpawn>... spawns) {
        super(structureId);
        this.spawns = Arrays.stream(spawns).toList();
    }

    public List<MapList<MapSpawn>> getSpawns() {
        return spawns;
    }
}
