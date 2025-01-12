package com.chaotic_loom.chaotic_minigames.core.data;

public class MapData {
    private final String structureId;
    private final MapList<MapSpawn> spawns;

    private long time;
    private boolean rain;

    private Runnable onLoad, onUnLoad;

    public MapData(String structureId, MapList<MapSpawn> spawns) {
        this.structureId = structureId;
        this.spawns = spawns;
    }

    public String getStructureId() {
        return structureId;
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

    public MapData executeOnLoad(Runnable runnable) {
        this.onLoad = runnable;
        return this;
    }

    public MapData executeOnUnLoad(Runnable runnable) {
        this.onUnLoad = runnable;
        return this;
    }

    public Runnable getOnLoad() {
        return onLoad;
    }

    public Runnable getOnUnLoad() {
        return onUnLoad;
    }
}
