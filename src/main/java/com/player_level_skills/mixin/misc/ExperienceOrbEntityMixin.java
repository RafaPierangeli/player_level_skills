package com.player_level_skills.mixin.misc;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.Skill;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.enchantment.EnchantmentHelper.getEquipmentLevel;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {

    @Inject(method = "repairPlayerGears", at = @At("HEAD"), cancellable = true)
    private void player_level_skills$blockMendingIfNeeded(ServerPlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir) {
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

        RegistryEntry<Enchantment> mendingEntry = player.getEntityWorld()
                .getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getEntry(Enchantments.MENDING.getValue())
                .orElse(null);

        if (mendingEntry == null) {
            return;
        }

        boolean hasMending = false;

        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (!stack.isEmpty() && EnchantmentHelper.getLevel(mendingEntry, stack) > 0) {
                hasMending = true;
                break;
            }
        }

        if (!hasMending) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = player.getEquippedStack(slot);
                if (!stack.isEmpty() && EnchantmentHelper.getLevel(mendingEntry, stack) > 0) {
                    hasMending = true;
                    break;
                }
            }
        }

        if (!hasMending) {
            return;
        }

        if (!levelManager.hasRequiredEnchantmentLevel(mendingEntry, 1)) {
            cir.setReturnValue(0);
        }
    }
}