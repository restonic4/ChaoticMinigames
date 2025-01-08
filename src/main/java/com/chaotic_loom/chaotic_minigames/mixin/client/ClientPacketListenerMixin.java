package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Redirect(
            method = "onDisconnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
                    ordinal = 2
            )
    )
    public void redirect(Minecraft instance, Screen screen, @Local(argsOnly = true) Component component) {
        instance.setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()), ClientPacketListener.GENERIC_DISCONNECT_MESSAGE, component));
    }
}
