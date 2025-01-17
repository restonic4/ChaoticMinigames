package com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop;

import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.data.SpawnerMapData;
import net.minecraft.core.BlockPos;

public class QuickDropMapData extends SpawnerMapData {
    private float startingHeight = 160;

    public QuickDropMapData(String structureId, MapList<MapSpawn> spawns) {
        super(structureId, spawns);
    }

    public float getStartingHeight() {
        return startingHeight;
    }

    public QuickDropMapData setStartingHeight(float startingHeight) {
        this.startingHeight = startingHeight;
        return this;
    }
}
