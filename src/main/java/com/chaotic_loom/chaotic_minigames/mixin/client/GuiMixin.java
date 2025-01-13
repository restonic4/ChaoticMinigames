package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
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
        Gui self = (Gui) (Object) this;
        Minecraft client = Minecraft.getInstance();

        if (KnownServerDataOnClient.currentMinigame == null && KnownServerDataOnClient.partyState != PartyStatus.State.AFTER_VOTING_INTERMISSION) {
            return;
        }

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        int topMargin = 10;
        int spacing = 5;

        int imageWidth = 100;
        int imageHeight = 50;

        int imageX = (screenWidth - imageWidth) / 2;
        int imageY = topMargin;

        int textY = imageY + imageHeight + spacing;

        guiGraphics.blit(
                KnownServerDataOnClient.currentMinigame.getSettings().getBannerImg(),
                imageX,
                imageY,
                0,
                0,
                imageWidth,
                imageHeight,
                imageWidth,
                imageHeight
        );

        String minigameName = KnownServerDataOnClient.currentMinigame.getSettings().getName().getString();
        guiGraphics.drawCenteredString(
                client.font,
                minigameName,
                screenWidth / 2,
                textY,
                0xFFFFFF
        );
    }
}
