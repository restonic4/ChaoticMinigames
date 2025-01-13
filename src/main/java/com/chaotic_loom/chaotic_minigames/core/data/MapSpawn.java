package com.chaotic_loom.chaotic_minigames.core.data;

import net.minecraft.core.BlockPos;

public class MapSpawn {
    private final BlockPos blockPos;
    private String extraData = "";

    public MapSpawn(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public MapSpawn(int x, int y, int z) {
        this.blockPos = new BlockPos(x, y, z);
    }

    public MapSpawn(BlockPos blockPos, String extraData) {
        this.blockPos = blockPos;
        this.extraData = extraData;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
