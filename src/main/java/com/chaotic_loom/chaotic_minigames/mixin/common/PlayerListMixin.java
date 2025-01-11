package com.chaotic_loom.chaotic_minigames.mixin.common;

import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;
import java.util.Optional;
import java.util.Random;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getGameRules()Lnet/minecraft/world/level/GameRules;"))
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        GameManager.getInstance().spawnPlayer(serverPlayer, true);
    }

    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z", ordinal = 1))
    public boolean respawn(Optional instance) {
        return false;
    }

    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    public void respawn(ServerGamePacketListenerImpl instance, Packet<?> packet) {
        //nothing
    }

    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setMainArm(Lnet/minecraft/world/entity/HumanoidArm;)V"))
    public void respawn(ServerPlayer instance, HumanoidArm humanoidArm, Operation<Void> original) {
        GameManager.getInstance().spawnPlayer(instance, false);

        original.call(instance, humanoidArm);
    }
}
