package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StructureBlockRenderer .class)
public class StructureBlockRendererMixin {
    /**
     * @reason Increase the distance that the bounding box can be seen up to 256 blocks
     * @author SamB440/Cotander
     */
    @ModifyConstant(method = "getViewDistance", constant = @Constant(intValue = 96), require = 0)
    @Environment(EnvType.CLIENT)
    public int getRenderDistance(int value) {
        return CMSharedConstants.STRUCTURE_BLOCK_MAX_SIZE / 2;
    }
}
