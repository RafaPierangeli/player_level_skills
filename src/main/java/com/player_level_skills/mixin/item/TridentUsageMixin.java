package com.player_level_skills.mixin.item;

import com.player_level_skills.level.LevelManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public abstract class TridentUsageMixin {

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void player_level_skills$captureTridentUser(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof ServerPlayerEntity player) {
            LevelManager.CURRENT_ATTACKER.set(player);
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("TAIL"))
    private void player_level_skills$releaseTridentUser(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        LevelManager.CURRENT_ATTACKER.remove();
    }

}
