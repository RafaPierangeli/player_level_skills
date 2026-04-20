package com.player_level_skills.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import net.minecraft.enchantment.provider.EnchantmentProvider;
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
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
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
        ServerPlayerEntity player = LevelManager.CURRENT_ATTACKER.get();


        if (player != null) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            if (!levelManager.hasRequiredEnchantmentLevel(enchantment, level)) {
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

//    @Inject(method = "applyAttributeModifiers(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"), cancellable = true)
//    private static void player_level_skills$applyLocationBasedEffects(
//            ItemStack stack, EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer, CallbackInfo ci
//    ) {
//            for (var entry : stack.getEnchantments().getEnchantmentEntries()) {
//                RegistryEntry<Enchantment> enchant = entry.getKey();
//                int level = entry.getIntValue();
//                    System.out.println("[DEBUG applyLocationBasedEffects] Bloqueando dano extra de: " + enchant.getIdAsString());
//                    ci.cancel();
//                    return;
//                }
//        }


    // funcionando para encantamentos de atributos
    @Inject(method = "applyAttributeModifiers(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"), cancellable = true)
    private static void player_level_skills$applyAttributeModifiers(
            ItemStack stack, EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer, CallbackInfo ci
    ) {
        // 1. Pega o player da Thread (Setado no Tick ou no ponto de atualização)
        ServerPlayerEntity player = LevelManager.CURRENT_MINER.get();
        if (player == null) return;

        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

        // 2. Verifica os encantamentos do item
        for (var entry : stack.getEnchantments().getEnchantmentEntries()) {
            RegistryEntry<Enchantment> enchant = entry.getKey();
            int level = entry.getIntValue();

            // 3. Aplica a sua restrição de nível
            if (!levelManager.hasRequiredEnchantmentLevel(enchant, level)) {
                //System.out.println("[DEBUG] Bloqueando bônus de: " + enchant.getIdAsString());
                ci.cancel();
                return;
            }
        }
    }

//    @Inject(method = "getFishingTimeReduction", at = @At("HEAD"), cancellable = true)
//    private static void player_level_skills$fishingTimeReduction(
//            ServerWorld world, ItemStack stack, Entity user, CallbackInfoReturnable<Float> cir
//    ) {
//        if (user instanceof ServerPlayerEntity player) {
//            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//            RegistryEntry<Enchantment> lureEntry = player.getEntityWorld().getRegistryManager()
//                    .getOrThrow(RegistryKeys.ENCHANTMENT)
//                    .getEntry(Enchantments.LURE.getValue())
//                    .orElse(null);
//
//            // Buscamos o nível de LURE (Isca) na vara de pesca
//            int level = EnchantmentHelper.getLevel(
//                    world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
//                            .getEntry(Enchantments.LURE.getValue()).orElse(null),
//                    stack
//            );
//
//            if (level > 0) {
//                // Se o player não tem nível, retornamos 0.0f (sem redução de tempo)
//                if (!levelManager.hasRequiredEnchantmentLevel(lureEntry, level)) {
//                    System.out.println("[DEBUG getFishingTimeReduction] Bloqueando bônus de: " + lureEntry);
//                    cir.setReturnValue(0.0f);
//                }
//            }
//        }
//    }


    @Inject(method = "getDamage", at = @At("RETURN"), cancellable = true)
    private static void player_level_skills$filterSpearDamage(
            ServerWorld world,
            ItemStack stack,
            Entity target,
            DamageSource damageSource,
            float baseDamage,
            CallbackInfoReturnable<Float> cir
    ) {
        // 1. Pegamos o player que causou o dano
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            float extraDamage = cir.getReturnValue() - baseDamage;

            if (extraDamage > 0) {
                // 2. Verificamos os encantamentos do item (Lança ou Espada)
                for (var entry : stack.getEnchantments().getEnchantmentEntries()) {
                    RegistryEntry<Enchantment> enchant = entry.getKey();
                    int level = entry.getIntValue();

                    // 3. Se o player não tem nível para o encantamento no item
                    if (!levelManager.hasRequiredEnchantmentLevel(enchant, level)) {
                        // System.out.println("[DEBUG Lança] Bloqueando dano extra de: " + enchant.getIdAsString());

                        // Retornamos apenas o dano base, ignorando o Sharpness/Afiação
                        cir.setReturnValue(baseDamage);
                        return;
                    }
                }
            }
        }
    }


    // para Power
//    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
//    private static void player_level_skills$filterEnchantmentDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseDamage, CallbackInfoReturnable<Float> cir) {
//        // 1. Pegamos o atacante diretamente da fonte do dano (funciona para flechas e espadas)
//        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
//            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//
//            RegistryEntry<Enchantment> powerEntry = player.getEntityWorld().getRegistryManager()
//                    .getOrThrow(RegistryKeys.ENCHANTMENT)
//                    .getEntry(Enchantments.POWER.getValue())
//                    .orElse(null);
//
//            if (powerEntry != null) {
//                int level = EnchantmentHelper.getLevel(powerEntry, stack);
//                if (level > 0) {
//                    if (!levelManager.hasRequiredEnchantmentLevel(powerEntry, level)) {
//                        cir.setReturnValue(baseDamage);
//                    }
//
//                }
//            }
//
//        }
//    }

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


    @Inject(method = "getTridentSpinAttackStrength", at = @At("HEAD"), cancellable = true)
    private static void player_level_skills$filterRiptideImpulse(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Float> ci) {


            LevelManager levelManager = ((LevelManagerAccess) user).getLevelManager();

            RegistryEntry<Enchantment> riptideEntry = user.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.RIPTIDE.getValue()).orElse(null);

            int level = EnchantmentHelper.getLevel(riptideEntry, stack);

                if (!levelManager.hasRequiredEnchantmentLevel(riptideEntry, level)) {
                    System.out.println("[DEBUG Riptide] Impulso anulado para: " + user.getName().getString()+ riptideEntry + level);
                    ci.cancel();
                }

    }


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
