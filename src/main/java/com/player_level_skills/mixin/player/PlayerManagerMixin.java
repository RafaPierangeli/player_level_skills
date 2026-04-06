package com.player_level_skills.mixin.player;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.init.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.PacketHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(
            method = "onPlayerConnect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    )
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

        if (levelManager.getSkillPoints() <= 0 && ConfigInit.CONFIG.startPoints > 0) {
            levelManager.setSkillPoints(ConfigInit.CONFIG.startPoints);
            PacketHelper.updateLevels(player);
        }
    }
}