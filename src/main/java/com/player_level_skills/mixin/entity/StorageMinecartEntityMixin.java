package com.player_level_skills.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import com.player_level_skills.access.PlayerDropAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(StorageMinecartEntity.class)
public class StorageMinecartEntityMixin {

    @Inject(method = "interact", at = @At("HEAD"))
    private void interactMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        ((PlayerDropAccess) player).resetKilledMobStat();
    }
}

