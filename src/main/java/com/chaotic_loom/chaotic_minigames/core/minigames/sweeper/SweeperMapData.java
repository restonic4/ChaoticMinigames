package com.chaotic_loom.chaotic_minigames.core.minigames.sweeper;

import com.chaotic_loom.chaotic_minigames.core.data.MapData;
import com.chaotic_loom.chaotic_minigames.core.data.MapList;
import com.chaotic_loom.chaotic_minigames.core.data.MapSpawn;
import com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.BulletChaosMapData;
import net.minecraft.core.BlockPos;

public class SweeperMapData extends MapData {
    private BlockPos center;
    private float minHeight = 22;
    private float barRadius = 25;
    private int spins = 15;

    public SweeperMapData(String structureId, MapList<MapSpawn> spawns) {
        super(structureId, spawns);
    }

    public BlockPos getCenter() {
        return center;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public float getBarRadius() {
        return barRadius;
    }

    public int getSpins() {
        return spins;
    }

    public SweeperMapData setCenter(BlockPos center) {
        this.center = center;
        return this;
    }

    public SweeperMapData setMinHeight(float height) {
        this.minHeight = height;
        return this;
    }

    public SweeperMapData setBarRadius(float barRadius) {
        this.barRadius = barRadius;
        return this;
    }

    public SweeperMapData setSpins(int spins) {
        this.spins = spins;
        return this;
    }
}
