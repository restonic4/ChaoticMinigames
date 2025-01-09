package com.chaotic_loom.chaotic_minigames.core.data;

public class MapData {
    private final String strucutreId;
    private final MapList<MapSpawn> spawns;

    public MapData(String strucutreId, MapList<MapSpawn> spawns) {
        this.strucutreId = strucutreId;
        this.spawns = spawns;
    }

    public String getStrucutreId() {
        return strucutreId;
    }

    public MapList<MapSpawn> getSpawns() {
        return spawns;
    }
}
