package com.chaotic_loom.chaotic_minigames.core.minigames.knock_em_off;

import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.MultipleSpawnerMapData;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class KnockEmOffMap extends MultipleSpawnerMapData {
    private Vector3f cameraPos;
    private Vector2f cameraRot;

    @SafeVarargs
    public KnockEmOffMap(String structureId, MapList<MapSpawn>... spawns) {
        super(structureId, spawns);
    }

    public Vector3f getCameraPos() {
        return cameraPos;
    }

    public KnockEmOffMap setCameraPos(Vector3f cameraPos) {
        this.cameraPos = cameraPos;
        return this;
    }

    public Vector2f getCameraRot() {
        return cameraRot;
    }

    public KnockEmOffMap setCameraRot(Vector2f cameraRot) {
        this.cameraRot = cameraRot;
        return this;
    }
}
