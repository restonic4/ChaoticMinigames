package com.chaotic_loom.chaotic_minigames.mixin.common;

import net.minecraft.server.commands.PlaceCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlaceCommand.class)
public class PlaceCommandMixin {
    @ModifyConstant(method = "register", constant = @Constant(intValue = 7), require = 0)
    private static int changeJigsawDepth(int value) {
        return Integer.MAX_VALUE;
    }
}
