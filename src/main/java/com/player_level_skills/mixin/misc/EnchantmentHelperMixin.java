package com.player_level_skills.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @WrapOperation(
            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;accept(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/enchantment/EnchantmentEffectContext;)V"
            )
    )
    private static void player_level_skills$filterEnchantmentEffects(
            EnchantmentHelper.ContextAwareConsumer instance,
            RegistryEntry<Enchantment> enchantment,
            int level,
            EnchantmentEffectContext context,
            Operation<Void> original
    ) {
        LivingEntity owner = context.owner();

        if (owner instanceof PlayerEntity playerEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();

            if (!levelManager.hasRequiredEnchantmentLevel(enchantment, level)) {
                return;
            }
        }

        original.call(instance, enchantment, level, context);
    }



    @Inject(
            method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void player_level_skills$blockRestrictedWeaponUse(
            ServerWorld world,
            Entity target,
            DamageSource damageSource,
            ItemStack weapon,
            CallbackInfo ci
    ) {
        if (weapon == null || weapon.isEmpty()) {
            return;
        }

        if (!(damageSource.getAttacker() instanceof PlayerEntity playerEntity)) {
            return;
        }

        LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();

        if (!levelManager.hasRequiredItemLevel(weapon.getItem())) {
            ci.cancel();
        }
    }

}
