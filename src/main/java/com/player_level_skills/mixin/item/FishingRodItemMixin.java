package com.player_level_skills.mixin.item;

import com.player_level_skills.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void player_level_skills$captureFishingUser(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (user instanceof ServerPlayerEntity player) {
            LevelManager.CURRENT_ATTACKER.set(player);
        }
    }

    @Inject(method = "use", at = @At("TAIL"))
    private void player_level_skills$releaseFishingUser(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LevelManager.CURRENT_ATTACKER.remove();
    }

}