package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.MusicManager;
import com.chaotic_loom.chaotic_minigames.core.ServerManager;
import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.util.EasingSystem;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    /*@Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int i, int j, CallbackInfo ci) {
        TitleScreen self = (TitleScreen) (Object) this;

        self.addRenderableWidget(
                Button.builder(
                        Component.translatable("gui.chaotic_minigames.title_screen.play"),
                        button -> {
                            ServerManager.matchServer();
                        }
                )
                .bounds(self.width / 2 - 100, i, 200, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.chaotic_minigames.title_screen.play.tooltip")))
                .build()
        );

        ci.cancel();
    }*/

    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        //guiGraphics.drawString(Minecraft.getInstance().font, "V:" + MusicManager.getCurrentVolume(), 0, 0, 0);
    }
}
