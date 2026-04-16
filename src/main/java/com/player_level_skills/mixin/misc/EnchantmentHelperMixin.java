package com.player_level_skills.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    //para frostwalker, fireAspect,
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



    // para sharpness, knockback, bane of A., Unbreaking -besta
//    @WrapOperation(
//            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;accept(Lnet/minecraft/registry/entry/RegistryEntry;I)V")
//    )
//    private static void player_level_skills$filterEnchantmentEffect(
//            EnchantmentHelper.Consumer instance, RegistryEntry<Enchantment> enchantment, int level, Operation<Void> original
//    ) {
//        // Tenta pegar o player da thread atual (definido no Mixin do attack ou tick)
//        ServerPlayerEntity player = LevelManager.CURRENT_ATTACKER.get();
//
//        if (player != null) {
//            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//            if (!levelManager.hasRequiredEnchantmentLevel(enchantment, level)) {
//                System.out.println("[DEBUG forEachEnchantment] Enchantment " + enchantment.getKey().toString());
//                // Ignora o encantamento se o player não tem nível
//                return;
//            }
//        }
//
//        original.call(instance, enchantment, level);
//    }

    @WrapOperation(
            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;accept(Lnet/minecraft/registry/entry/RegistryEntry;I)V")
    )
    private static void player_level_skills$filterEnchantmentEffect(
            EnchantmentHelper.Consumer instance,
            RegistryEntry<Enchantment> enchantment,
            int level,
            Operation<Void> original
    ) {
        // Tenta pegar primeiro o minerador, se não houver, tenta o atacante
        ServerPlayerEntity player = LevelManager.CURRENT_MINER.get();
        if (player == null) {
            player = LevelManager.CURRENT_ATTACKER.get();
        }

        if (player != null) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            if (!levelManager.hasRequiredEnchantmentLevel(enchantment, level)) {
                System.out.println("[DEBUG] Bloqueando: " + enchantment.getKey().toString());
                return;
            }
        }

        original.call(instance, enchantment, level);
    }


//Compile, but not work
//    @Inject(method = "forEachEnchantment(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V", at = @At("HEAD"), cancellable = true)
//    private static void player_level_skills$manualForEach(LivingEntity entity, net.minecraft.enchantment.EnchantmentHelper.ContextAwareConsumer consumer, CallbackInfo ci) {
//        if (entity instanceof ServerPlayerEntity player) {
//            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//
//            // O Minecraft original percorre TODOS os slots de equipamento (mãos e armaduras)
//            for (EquipmentSlot slot : EquipmentSlot.values()) {
//                ItemStack stack = entity.getEquippedStack(slot);
//                if (stack.isEmpty()) continue;
//
//                RegistryEntry<Enchantment> respirationEntry = player.getEntityWorld().getRegistryManager()
//                        .getOrThrow(RegistryKeys.ENCHANTMENT)
//                        .getEntry(Enchantments.RESPIRATION.getValue())
//                        .orElse(null);
//
//                if (respirationEntry != null) {
//                    int level = EnchantmentHelper.getLevel(respirationEntry, stack);
//
//                        if (!levelManager.hasRequiredEnchantmentLevel(respirationEntry, level)) {
//                            System.out.println("[DEBUG forEachEnchantment] Enchantment " + respirationEntry.getKey().toString());
//                            ci.cancel();
//                        }
//
//
//                }
//            }
//        }
//    }


    // para Power
    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private static void player_level_skills$filterEnchantmentDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseDamage, CallbackInfoReturnable<Float> cir) {
        // 1. Pegamos o atacante diretamente da fonte do dano (funciona para flechas e espadas)
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

            RegistryEntry<Enchantment> powerEntry = player.getEntityWorld().getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.POWER.getValue())
                    .orElse(null);

            if (powerEntry != null) {
                int level = EnchantmentHelper.getLevel(powerEntry, stack);
                if (level > 0) {
                    if (!levelManager.hasRequiredEnchantmentLevel(powerEntry, level)) {
                        cir.setReturnValue(baseDamage);
                    }

                }
            }

        }
    }

    @Inject(method = "modifyKnockback", at = @At("RETURN"), cancellable = true)
    private static void player_level_skills$cancelKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseKnockback, CallbackInfoReturnable<Float> cir) {
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            var knockbackEntry = player.getRegistryManager() .getOrThrow(RegistryKeys.ENCHANTMENT) .getEntry(Enchantments.PUNCH.getValue()) .orElse(null);
            int level = EnchantmentHelper.getLevel(knockbackEntry, stack);
            if (level > 0 && !levelManager.hasRequiredEnchantmentLevel(knockbackEntry, level)) {
                // Retorna o knockback base (sem o bônus do encantamento)
                cir.setReturnValue(baseKnockback);
            }
        }
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



//    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
//    private static void player_level_skills$filterAllEnchantments(RegistryEntry<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
//        int originalLevel = cir.getReturnValue();
//        System.out.println("[DEBUG getLevel] Method called");
//
//        if (originalLevel > 0) {
//            // Tenta pegar o player do ThreadLocal (que você já seta no 'use' ou no 'attack')
//            ServerPlayerEntity player = LevelManager.CURRENT_ATTACKER.get();
//
//            if (player != null) {
//                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//                if (!levelManager.hasRequiredEnchantmentLevel(enchantment, originalLevel)) {
//                    // Se o player não tem nível, o encantamento "não existe" (nível 0)
//                    cir.setReturnValue(0);
//                }
//            }
//        }
//    }


//    @Inject(
//            method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;)V",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private static void player_level_skills$blockRestrictedEnchantments(
//            ServerWorld world,
//            Entity target,
//            DamageSource damageSource,
//            ItemStack weapon,
//            CallbackInfo ci
//    ) {
//        if (weapon == null || weapon.isEmpty()) {
//            return;
//        }
//
//        if (!(damageSource.getAttacker() instanceof PlayerEntity playerEntity)) {
//            return;
//        }
//
//        LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
//
//        var enchantments = EnchantmentHelper.getEnchantments(weapon);
//
//        for (var entry : enchantments.getEnchantmentEntries()) {
//            if (!levelManager.hasRequiredEnchantmentLevel(entry.getKey(), entry.getIntValue())) {
//                System.out.println("[DEBUG] Enchantment bloqueado: "+entry.getKey().toString());
//                ci.cancel();
//                return;
//            }
//        }
//    }

}
