package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @Redirect(
            method = "onDisconnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
                    ordinal = 2
            )
    )
    private void redirectScreen(Minecraft instance, Screen screen) {
        instance.setScreen(new ServerListScreen(screen));
    }
}
