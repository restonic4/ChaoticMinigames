package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        //guiGraphics.drawString(Minecraft.getInstance().font, "", guiGraphics.guiWidth() / 2, 0, 0);
    }
}
