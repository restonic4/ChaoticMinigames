package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos;

import com.chaotic_loom.chaotic_minigames.core.data.SpawnerMapData;
import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import net.minecraft.core.BlockPos;

public class BulletChaosMapData extends SpawnerMapData {
    private BlockPos center;

    public BulletChaosMapData(String structureId, MapList<MapSpawn> spawns) {
        super(structureId, spawns);
    }

    public BlockPos getCenter() {
        return center;
    }

    public BulletChaosMapData setCenter(BlockPos center) {
        this.center = center;
        return this;
    }
}
