package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.quickplay.QuickPlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QuickPlay.class)
public class QuickPlayMixin {
    @Redirect(
            method = "joinMultiplayerWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/ConnectScreen;startConnecting(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;Z)V"
            )
    )
    private static void redirect(Screen screen, Minecraft minecraft, ServerAddress serverAddress, ServerData serverData, boolean bl) {
        ConnectScreen.startConnecting(new ServerListScreen(new TitleScreen()), minecraft, serverAddress, serverData, true);
    }
}
