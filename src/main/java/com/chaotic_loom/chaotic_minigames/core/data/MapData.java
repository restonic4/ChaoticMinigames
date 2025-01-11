package com.chaotic_loom.chaotic_minigames.core.data;

public class MapData {
    private final String strucutreId;
    private final MapList<MapSpawn> spawns;

    private long time;
    private boolean rain;

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

    public MapData setTime(long time) {
        this.time = time;
        return this;
    }

    public MapData setRain(boolean rain) {
        this.rain = rain;
        return this;
    }

    public long getTime() {
        return time;
    }

    public boolean isRain() {
        return rain;
    }
}
