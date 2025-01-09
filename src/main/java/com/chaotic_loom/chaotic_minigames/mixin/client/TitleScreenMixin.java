package com.chaotic_loom.chaotic_minigames.mixin.client;

import com.chaotic_loom.chaotic_minigames.core.ServerNetworkManager;
import net.fabricmc.fabric.mixin.resource.loader.client.CreateWorldScreenMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import com.chaotic_loom.chaotic_minigames.entrypoints.Client;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int i, int j, CallbackInfo ci) {
        TitleScreen self = (TitleScreen) (Object) this;

        self.addRenderableWidget(
                Button.builder(
                        Component.translatable("gui.chaotic_minigames.title_screen.play"),
                        button -> {
                            ServerNetworkManager.matchServer();
                        }
                )
                .bounds(self.width / 2 - 100, i, 200, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.chaotic_minigames.title_screen.play.tooltip")))
                .build()
        );

        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();

        if (Client.areKeysPressed(client, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_ESCAPE)) {
            client.setScreen(new SelectWorldScreen(client.screen));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        //guiGraphics.drawString(Minecraft.getInstance().font, "V:" + MusicManager.getCurrentVolume(), 0, 0, 0);
    }
}
