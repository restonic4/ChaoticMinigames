package com.chaotic_loom.chaotic_minigames.core.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LobbyScreen extends Screen {
    public LobbyScreen(Component component) {
        super(component);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, -16750151, -16731921);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 90, 16777215);

        super.render(guiGraphics, i, j, f);
    }
}
