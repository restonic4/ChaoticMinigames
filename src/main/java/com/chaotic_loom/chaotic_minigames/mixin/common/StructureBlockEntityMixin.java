package com.chaotic_loom.chaotic_minigames.mixin.common;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(StructureBlockEntity.class)
public class StructureBlockEntityMixin {
    @ModifyConstant(method = "load", constant = @Constant(intValue = 48), require = 0)
    public int readNbtUpper(int value) {
        return CMSharedConstants.STRUCTURE_BLOCK_MAX_SIZE;
    }

    @ModifyConstant(method = "load", constant = @Constant(intValue = -48), require = 0)
    public int readNbtLower(int value) {
        return -CMSharedConstants.STRUCTURE_BLOCK_MAX_SIZE;
    }

    /**
     * @author SamB440
     * @reason Optimise searching of structure blocks by searching from the structure block outwards instead of from min corner to max (and reduce streams).
     */
    @Overwrite
    private Stream<BlockPos> getRelatedCorners(BlockPos min, BlockPos max) {
        StructureBlockEntity blockEntity = (StructureBlockEntity) (Object) this;
        final Level level = blockEntity.getLevel();
        if (level == null) return Stream.empty();
        final BlockPos middle = blockEntity.getBlockPos();
        List<BlockPos> blocks = new ArrayList<>(2);
        final int maxSearch = detectSize(-1) + 1;
        BlockPos.findClosestMatch(middle, maxSearch, Math.min(maxSearch, level.getHeight() + 1), pos -> {
            if (level.getBlockEntity(pos) instanceof StructureBlockEntity block) {
                if (block.getMode() == StructureMode.CORNER && Objects.equals(blockEntity.getStructureName(), block.getStructureName())) {
                    blocks.add(block.getBlockPos());
                }
            }
            return blocks.size() == 2;
        });
        return blocks.stream();
    }

    @ModifyConstant(method = "detectSize", constant = @Constant(intValue = 80), require = 0)
    public int detectSize(int value) {
        return CMSharedConstants.STRUCTURE_BLOCK_MAX_SIZE;
    }
}
