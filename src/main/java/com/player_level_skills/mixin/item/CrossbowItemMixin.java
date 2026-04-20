package com.player_level_skills.mixin.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.BonusHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "shoot", at = @At("TAIL"))
    private void shootMixin(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo info) {
        BonusHelper.crossbowBonus(shooter, projectile);
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void player_level_skills$captureExtraProjectilesBeforeUse(
            World world,
            PlayerEntity user,
            Hand hand,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        if (user instanceof ServerPlayerEntity player) {
            LevelManager.CURRENT_ATTACKER.set(player);
        }
    }

    @Inject(method = "use", at = @At("TAIL"))
    private void player_level_skills$releaseProjectilesBeforeUse(
            World world,
            PlayerEntity user,
            Hand hand,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        if (user instanceof ServerPlayerEntity player) {
            LevelManager.CURRENT_ATTACKER.remove();

        }
    }
}
