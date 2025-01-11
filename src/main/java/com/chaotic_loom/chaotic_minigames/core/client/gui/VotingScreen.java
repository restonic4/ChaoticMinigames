package com.chaotic_loom.chaotic_minigames.core.client.gui;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.KnownServerDataOnClient;
import com.chaotic_loom.chaotic_minigames.networking.packets.client_to_server.SendVote;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VotingScreen extends Screen {
    public VotingScreen() {
        super(Component.translatable("gui.chaotic_minigames.voting_screen.title"));
    }

    @Override
    protected void init() {
        int width = this.width;
        int height = this.height;

        int boxWidth = width / 4;
        int boxHeight = height / 2;

        int spacing = width / 20;
        int contentHeight = boxHeight - 20;

        int x1 = spacing;
        int x2 = x1 + boxWidth + spacing;
        int x3 = x2 + boxWidth + spacing;
        int y1 = height / 6;

        addRenderableWidget(Button.builder(Component.translatable("gui.chaotic_minigames.vote"), button -> {
            handleVote(1);
        }).bounds(x1, y1 + contentHeight, boxWidth, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.chaotic_minigames.vote"), button -> {
            handleVote(2);
        }).bounds(x2, y1 + contentHeight, boxWidth, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.chaotic_minigames.vote"), button -> {
            handleVote(3);
        }).bounds(x3, y1 + contentHeight, boxWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);

        int width = this.width;
        int height = this.height;

        int boxWidth = width / 4;
        int boxHeight = height / 2;

        int spacing = width / 20;
        int contentHeight = boxHeight - 20;

        int x1 = spacing;
        int x2 = x1 + boxWidth + spacing;
        int x3 = x2 + boxWidth + spacing;
        int y1 = height / 6;

        if (KnownServerDataOnClient.minigameIdToVote3 == null) {
            guiGraphics.drawCenteredString(this.font, Component.literal("Loading..."), x1 + boxWidth / 2, y1 - 10, 0xFFFFFF);
            guiGraphics.drawCenteredString(this.font, Component.literal("Loading..."), x2 + boxWidth / 2, y1 - 10, 0xFFFFFF);
            guiGraphics.drawCenteredString(this.font, Component.literal("Loading..."), x3 + boxWidth / 2, y1 - 10, 0xFFFFFF);

            super.render(guiGraphics, mouseX, mouseY, delta);
            return;
        }

        guiGraphics.drawCenteredString(this.font, KnownServerDataOnClient.minigameNameToVote1, x1 + boxWidth / 2, y1 - 10, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, KnownServerDataOnClient.minigameNameToVote2, x2 + boxWidth / 2, y1 - 10, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, KnownServerDataOnClient.minigameNameToVote3, x3 + boxWidth / 2, y1 - 10, 0xFFFFFF);

        guiGraphics.blit(KnownServerDataOnClient.minigameImageToVote1, x1, y1, 0, 0, boxWidth, contentHeight, boxWidth, contentHeight);
        guiGraphics.blit(KnownServerDataOnClient.minigameImageToVote2, x2, y1, 0, 0, boxWidth, contentHeight, boxWidth, contentHeight);
        guiGraphics.blit(KnownServerDataOnClient.minigameImageToVote3, x3, y1, 0, 0, boxWidth, contentHeight, boxWidth, contentHeight);

        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    private void handleVote(int option) {
        String minigameId = (option == 1) ? KnownServerDataOnClient.minigameIdToVote1 : ((option == 2) ? KnownServerDataOnClient.minigameIdToVote2 : KnownServerDataOnClient.minigameIdToVote3);

        System.out.println("Voted: " + minigameId);

        SendVote.sendToServer(minigameId);

        onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
