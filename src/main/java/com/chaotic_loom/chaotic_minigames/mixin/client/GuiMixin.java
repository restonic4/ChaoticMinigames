package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.data.PartyStatus;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.under_control.util.EasingSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Unique
    private long startStartTime;
    @Unique
    private long animEndTime;

    private boolean bothAnimsFinished;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        Gui self = (Gui) (Object) this;
        Minecraft client = Minecraft.getInstance();

        GenericMinigame minigame = (KnownServerDataOnClient.nextMinigame != null) ? KnownServerDataOnClient.nextMinigame : KnownServerDataOnClient.currentMinigame;

        if (minigame == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (KnownServerDataOnClient.partyState == PartyStatus.State.AFTER_VOTING_INTERMISSION && bothAnimsFinished) {
            bothAnimsFinished = false;

            startStartTime = 0;
            animEndTime = 0;
        }

        if (bothAnimsFinished && currentTime > animEndTime) {
            return;
        }

        if (KnownServerDataOnClient.partyState == PartyStatus.State.AFTER_VOTING_INTERMISSION && startStartTime == 0) {
            startStartTime = currentTime;
            animEndTime = startStartTime + 2000;
        }

        if (KnownServerDataOnClient.partyState == PartyStatus.State.PLAYING && currentTime >= animEndTime) {
            startStartTime = currentTime;
            animEndTime = startStartTime + 2000;
            bothAnimsFinished = true;
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

        int titleX = screenWidth / 2;

        int animatedIMGStartingX = -imageWidth;
        int animatedTitleStartingX = -imageWidth;

        if (KnownServerDataOnClient.partyState == PartyStatus.State.PLAYING) {
            animatedIMGStartingX = imageX;
            animatedTitleStartingX = titleX;

            imageX = screenWidth;
            titleX = screenWidth + imageWidth;
        }

        int animatedIMGX = (int) EasingSystem.getEasedValue(currentTime, startStartTime, animEndTime, animatedIMGStartingX, imageX, EasingSystem.EasingType.CUBIC_OUT);
        int animatedTitleX = (int) EasingSystem.getEasedValue(currentTime, startStartTime, animEndTime, animatedTitleStartingX, titleX, EasingSystem.EasingType.CUBIC_OUT);

        guiGraphics.blit(
                minigame.getSettings().getBannerImg(),
                animatedIMGX,
                imageY,
                0,
                0,
                imageWidth,
                imageHeight,
                imageWidth,
                imageHeight
        );

        String minigameName = minigame.getSettings().getName().getString();
        guiGraphics.drawCenteredString(
                client.font,
                minigameName,
                animatedTitleX,
                textY,
                0xFFFFFF
        );
    }
}
