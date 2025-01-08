package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.client.gui.ServerListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.network.chat.CommonComponents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SafetyScreen.class)
public class SafetyScreenMixin {
    @Shadow @Final private Screen previous;

    /**
     * @author restonic4
     * @reason idk
     */
    @Overwrite
    public void initButtons(int i) {
        SafetyScreen self = (SafetyScreen) (Object) this;

        self.addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, button -> {
            if (self.stopShowing.selected()) {
                self.minecraft.options.skipMultiplayerWarning = true;
                self.minecraft.options.save();
            }

            self.minecraft.setScreen(new ServerListScreen(self.previous));
        }).bounds(self.width / 2 - 155, 100 + i, 150, 20).build());
        self.addRenderableWidget(
                Button.builder(CommonComponents.GUI_BACK, button -> self.minecraft.setScreen(self.previous)).bounds(self.width / 2 - 155 + 160, 100 + i, 150, 20).build()
        );
    }
}
